import { Tool } from './types';

export const TOOLS: Tool[] = [
  {
    name: 'get_glossary',
    description:
      'Returns a glossary of business terms and their definitions related to the HR database',
    inputSchema: {
      type: 'object',
      properties: {},
    },
  },
  {
    name: 'show_tables',
    description: 'Shows all tables available in the database',
    inputSchema: {
      type: 'object',
      properties: {},
    },
  },
  {
    name: 'describe_table',
    description: 'Describes the structure and fields of a specific table',
    inputSchema: {
      type: 'object',
      properties: {
        table_name: {
          type: 'string',
          description: 'The name of the table to describe',
        },
      },
      required: ['table_name'],
    },
  },
  {
    name: 'run_query',
    description:
      'Executes a SELECT query to retrieve data insights. Only SELECT statements are allowed.',
    inputSchema: {
      type: 'object',
      properties: {
        query: {
          type: 'string',
          description: 'The SELECT SQL query to execute',
        },
      },
      required: ['query'],
    },
  },
  {
    name: 'get_highest_paid_employee',
    description:
      'Returns information about the highest paid employee in the company',
    inputSchema: {
      type: 'object',
      properties: {},
    },
  },
  {
    name: 'promotion_gap',
    description:
      'List employees ordered by time since their last promotion (or first contract if never promoted). Useful for finding employees longest without promotion.',
    inputSchema: {
      type: 'object',
      properties: {
        limit: {
          type: 'integer',
          description: 'How many employees to return (default 10).',
          minimum: 1,
        },
      },
    },
  },
  {
    name: 'recent_promotions',
    description:
      'List the most recently promoted employees, ordered by promotion date descending.',
    inputSchema: {
      type: 'object',
      properties: {
        limit: {
          type: 'integer',
          description: 'How many recent promotions to return (default 5).',
          minimum: 1,
        },
      },
    },
  },
  {
    name: 'avg_promotion_interval',
    description:
      'Calculate the average number of days between promotions across all employees.',
    inputSchema: {
      type: 'object',
      properties: {},
    },
  },
  {
    name: 'get_employee_by_id',
    description: 'Fetch detailed employee info by ID.',
    inputSchema: {
      type: 'object',
      properties: {
        id: { type: 'integer', description: 'The employee ID' },
      },
      required: ['id'],
    },
  },
  {
    name: 'search_employees_by_name',
    description: 'Search for employees by partial or full name.',
    inputSchema: {
      type: 'object',
      properties: {
        name: { type: 'string', description: 'The name to search for' },
      },
      required: ['name'],
    },
  },
] as const;

export type ToolName = (typeof TOOLS)[number]['name'];
