export interface MCPRequest {
  method: string;
  params?: {
    name?: string;
    arguments?: Record<string, any>;
  };
}

export interface MCPResponse {
  content: Array<{
    type: string;
    text: string;
  }>;
}

export interface MCPError {
  error: {
    code: string;
    message: string;
  };
}

export interface Tool {
  name: string;
  description: string;
  inputSchema: {
    type: 'object';
    properties: Record<string, any>;
    required?: string[];
  };
}

export interface ChatRequest {
  message: string;
  conversation_history?: Array<{
    role: 'user' | 'assistant';
    content: string;
  }>;
}

export interface ChatResponse {
  response: string;
  tools_used: string[];
}
