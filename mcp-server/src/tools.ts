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
];
