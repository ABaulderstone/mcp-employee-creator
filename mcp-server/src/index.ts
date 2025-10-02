import express, { Request, Response } from 'express';
import cors from 'cors';
import dotenv from 'dotenv';
import Anthropic from '@anthropic-ai/sdk';
import type {
  MessageParam,
  TextBlock,
  ToolUseBlock,
  ContentBlock,
  Message,
} from '@anthropic-ai/sdk/resources/messages';
import {
  MCPRequest,
  MCPResponse,
  MCPError,
  ChatRequest,
  ChatResponse,
} from './types';
import { TOOLS } from './tools';
import {
  handleGetGlossary,
  handleShowTables,
  handleDescribeTable,
  handleRunQuery,
  handleGetHighestPaidEmployee,
  executeTool,
  handleGetEmployeesByPromotionGap,
  handleGetAveragePromotionInterval,
  handleGetMostRecentPromotions,
  handleGetEmployeeById,
  handleSearchEmployeesByName,
} from './handlers';

import { systemPrompt } from './prompt';

dotenv.config();

// Debug: Check if env vars are loaded
console.log('Environment check:');
console.log('DB_HOST:', process.env.DB_HOST);
console.log('DB_USER:', process.env.DB_USER);
console.log('DB_NAME:', process.env.DB_NAME);
console.log(
  'ANTHROPIC_API_KEY:',
  process.env.ANTHROPIC_API_KEY ? 'Set' : 'Not set'
);

const app = express();
const PORT = process.env.PORT || 3000;

const anthropic = new Anthropic({
  apiKey: process.env.ANTHROPIC_API_KEY,
});

app.use(cors());
app.use(express.json());

app.get('/health', (req: Request, res: Response) => {
  res.json({ status: 'ok', timestamp: new Date().toISOString() });
});

app.post('/tools/list', (req: Request, res: Response) => {
  res.json({
    tools: TOOLS,
  });
});

app.post('/tools/call', async (req: Request, res: Response) => {
  console.log('tool call');
  try {
    const mcpRequest: MCPRequest = req.body;
    const toolName = mcpRequest.params?.name;
    const args = mcpRequest.params?.arguments || {};
    console.log(req.body);
    if (!toolName) {
      const error: MCPError = {
        error: {
          code: 'invalid_request',
          message: 'Tool name is required',
        },
      };
      return res.status(400).json(error);
    }

    let response: MCPResponse;

    switch (toolName) {
      case 'get_glossary':
        response = await handleGetGlossary();
        break;

      case 'show_tables':
        response = await handleShowTables();
        break;

      case 'describe_table':
        if (!args.table_name) {
          throw new Error('table_name argument is required');
        }
        response = await handleDescribeTable(args.table_name);
        break;

      case 'run_query':
        if (!args.query) {
          throw new Error('query argument is required');
        }
        response = await handleRunQuery(args.query);
        break;

      case 'get_highest_paid_employee':
        response = await handleGetHighestPaidEmployee();
        break;
      case 'promotion_gap':
        response = await handleGetEmployeesByPromotionGap(args.limit);
        break;

      case 'avg_promotion_interval':
        response = await handleGetAveragePromotionInterval();
        break;

      case 'recent_promotions':
        response = await handleGetMostRecentPromotions(args.limit);
        break;

      case 'get_employee_by_id':
        if (!args.id) {
          throw new Error('id argument is required');
        }
        response = await handleGetEmployeeById(args.id);
        break;

      case 'search_employees_by_name':
        if (!args.name) {
          throw new Error('name argument is required');
        }
        response = await handleSearchEmployeesByName(args.name);
        break;

      default:
        const error: MCPError = {
          error: {
            code: 'tool_not_found',
            message: `Tool '${toolName}' not found`,
          },
        };
        return res.status(404).json(error);
    }

    res.json(response);
  } catch (err) {
    const error: MCPError = {
      error: {
        code: 'execution_error',
        message: err instanceof Error ? err.message : 'Unknown error occurred',
      },
    };
    res.status(500).json(error);
  }
});

app.post('/chat', async (req: Request, res: Response) => {
  try {
    const chatRequest: ChatRequest = req.body;

    if (!chatRequest.message) {
      return res.status(400).json({
        error: {
          code: 'invalid_request',
          message: 'Message is required',
        },
      });
    }

    const messages: MessageParam[] = [
      ...(chatRequest.conversation_history || []).map((msg) => ({
        role: msg.role,
        content: msg.content,
      })),
      {
        role: 'user' as const,
        content: chatRequest.message,
      },
    ];

    const toolsUsed: string[] = [];

    let response: Message = await anthropic.messages.create({
      model: 'claude-3-haiku-20240307',
      max_tokens: 4096,
      system: systemPrompt,
      tools: TOOLS.map((tool) => ({
        name: tool.name,
        description: tool.description,
        input_schema: tool.inputSchema,
      })),
      messages,
    });

    // Loop while Claude wants to use tools
    while (response.stop_reason === 'tool_use') {
      const toolUseBlocks = response.content.filter(
        (block): block is ToolUseBlock => block.type === 'tool_use'
      );

      const toolResults: ContentBlock[] = [];

      for (const toolUse of toolUseBlocks) {
        toolsUsed.push(toolUse.name);

        try {
          const toolResponse = await executeTool(
            toolUse.name,
            toolUse.input as Record<string, any>
          );
          const resultText = toolResponse.content.map((c) => c.text).join('\n');

          toolResults.push({
            type: 'tool_result',
            tool_use_id: toolUse.id,
            content: resultText,
          } as any);
        } catch (error) {
          toolResults.push({
            type: 'tool_result',
            tool_use_id: toolUse.id,
            content: `Error: ${
              error instanceof Error ? error.message : 'Unknown error'
            }`,
            is_error: true,
          } as any);
        }
      }

      // Add assistant message with tool use
      messages.push({
        role: 'assistant',
        content: response.content,
      });

      // Add user message with tool results
      messages.push({
        role: 'user',
        content: toolResults,
      });

      // Continue conversation
      response = await anthropic.messages.create({
        model: 'claude-3-haiku-20240307',
        max_tokens: 4096,
        system: systemPrompt,
        tools: TOOLS.map((tool) => ({
          name: tool.name,
          description: tool.description,
          input_schema: tool.inputSchema,
        })),
        messages,
      });
    }

    // Extract final text response
    const textBlocks = response.content.filter(
      (block): block is TextBlock => block.type === 'text'
    );
    const finalResponse = textBlocks.map((block) => block.text).join('\n');

    const chatResponse: ChatResponse = {
      response: finalResponse,
      tools_used: toolsUsed,
    };

    res.json(chatResponse);
  } catch (err) {
    console.error('Chat error:', err);
    const error: MCPError = {
      error: {
        code: 'chat_error',
        message: err instanceof Error ? err.message : 'Unknown error occurred',
      },
    };
    res.status(500).json(error);
  }
});

app.listen(PORT, () => {
  console.log(`MCP Server running on http://localhost:${PORT}`);
  console.log(`Available endpoints:`);
  console.log(`  GET  /health - Health check`);
  console.log(`  POST /tools/list - List available tools`);
  console.log(`  POST /tools/call - Execute a tool`);
  console.log(`  POST /chat - Chat with LLM (uses tools automatically)`);
});
