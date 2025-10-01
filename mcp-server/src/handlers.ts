import { getPool } from './config/database';
import { GLOSSARY } from './glossary';
import { MCPResponse, MCPError } from './types';

export const handleGetGlossary = async (): Promise<MCPResponse> => {
  const glossaryText = Object.entries(GLOSSARY)
    .map(([term, definition]) => `**${term}**: ${definition}`)
    .join('\n\n');

  return {
    content: [
      {
        type: 'text',
        text: `# HR Database Glossary\n\n${glossaryText}`,
      },
    ],
  };
};

export const handleShowTables = async (): Promise<MCPResponse> => {
  const pool = getPool();
  const [rows] = await pool.query('SHOW TABLES');

  const tableNames = (rows as any[]).map((row) => Object.values(row)[0]);
  const tablesText = tableNames.join('\n');

  return {
    content: [
      {
        type: 'text',
        text: `# Available Tables\n\n${tablesText}`,
      },
    ],
  };
};

export const handleDescribeTable = async (
  tableName: string
): Promise<MCPResponse> => {
  const pool = getPool();

  // Validate table name to prevent SQL injection
  const validTables = ['employees', 'contracts', 'departments'];
  if (!validTables.includes(tableName)) {
    throw new Error(
      `Invalid table name. Must be one of: ${validTables.join(', ')}`
    );
  }

  const [rows] = await pool.query(`DESCRIBE ${tableName}`);

  const description = (rows as any[])
    .map(
      (row) =>
        `- **${row.Field}** (${row.Type}): ${
          row.Null === 'YES' ? 'Nullable' : 'Not Null'
        }${row.Key ? `, Key: ${row.Key}` : ''}${
          row.Extra ? `, ${row.Extra}` : ''
        }`
    )
    .join('\n');

  return {
    content: [
      {
        type: 'text',
        text: `# Table: ${tableName}\n\n${description}`,
      },
    ],
  };
};

export const handleRunQuery = async (query: string): Promise<MCPResponse> => {
  const pool = getPool();

  // Validate it's a SELECT query
  const trimmedQuery = query.trim().toUpperCase();
  console.log('Query ran: ', trimmedQuery);
  if (!trimmedQuery.startsWith('SELECT')) {
    throw new Error('Only SELECT queries are allowed');
  }

  // Additional safety checks
  const dangerousKeywords = [
    'DROP',
    'DELETE',
    'INSERT',
    'UPDATE',
    'ALTER',
    'CREATE',
    'TRUNCATE',
  ];
  if (dangerousKeywords.some((keyword) => trimmedQuery.includes(keyword))) {
    throw new Error('Query contains forbidden keywords');
  }

  const [rows] = await pool.query(query);

  const resultText = JSON.stringify(rows, null, 2);

  return {
    content: [
      {
        type: 'text',
        text: `# Query Results\n\n\`\`\`json\n${resultText}\n\`\`\``,
      },
    ],
  };
};

export const handleGetHighestPaidEmployee = async (): Promise<MCPResponse> => {
  const pool = getPool();

  const query = `
    SELECT e.id, e.first_name, e.last_name, e.date_of_birth, d.name, c.job_title, c.salary
    FROM employees e
    JOIN contracts c ON e.id = c.employee_id
    JOIN departments d ON d.id = c.department_id
    ORDER BY c.salary DESC
    LIMIT 1
  `;

  const [rows] = await pool.query(query);
  const result = (rows as any[])[0];

  if (!result) {
    return {
      content: [
        {
          type: 'text',
          text: 'No employees found in the database.',
        },
      ],
    };
  }

  const resultText = `# Highest Paid Employee

**Name**: ${result.first_name} ${result.last_name}
**Employee ID**: ${result.id}
**Date of Birth**: ${result.date_of_birth}
**Department**: ${result.name}
**Job Title**: ${result.job_title}
**Salary**: ${result.salary.toLocaleString()}`;

  return {
    content: [
      {
        type: 'text',
        text: resultText,
      },
    ],
  };
};

// Helper function to execute a tool by name
export const executeTool = async (
  toolName: string,
  args: Record<string, any> = {}
): Promise<MCPResponse> => {
  switch (toolName) {
    case 'get_glossary':
      return await handleGetGlossary();

    case 'show_tables':
      return await handleShowTables();

    case 'describe_table':
      if (!args.table_name) {
        throw new Error('table_name argument is required');
      }
      return await handleDescribeTable(args.table_name);

    case 'run_query':
      if (!args.query) {
        throw new Error('query argument is required');
      }
      return await handleRunQuery(args.query);

    case 'get_highest_paid_employee':
      return await handleGetHighestPaidEmployee();

    default:
      throw new Error(`Tool '${toolName}' not found`);
  }
};
