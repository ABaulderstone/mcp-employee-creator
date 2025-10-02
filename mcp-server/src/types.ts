import { TOOLS } from './tools';

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

export interface EnrichedEmployee {
  id: number;
  firstName: string;
  lastName: string;
  dateOfBirth: string; // ISO date string, e.g. "1980-04-03"
  departmentName: string;
  jobTitle: string;
  salary: number;
}

export interface EmployeeSummary {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
}

export interface PaginatedEmployeeResponse {
  currentPage: number;
  totalPages: number;
  totalResults: number;
  resultsPerPage: number;
  nextPage: number | null;
  previousPage: number | null;
  data: EmployeeSummary[];
}
