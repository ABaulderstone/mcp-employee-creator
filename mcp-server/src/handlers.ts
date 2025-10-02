import { getPool } from './config/database';
import { GLOSSARY } from './glossary';
import { ToolName } from './tools';
import {
  MCPResponse,
  MCPError,
  EnrichedEmployee,
  PaginatedEmployeeResponse,
  EmployeeSummary,
} from './types';

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

export const handleGetEmployeesByPromotionGap = async (
  limit = 10
): Promise<MCPResponse> => {
  const pool = getPool();

  const query = `
    WITH ordered_contracts AS (
        SELECT
            c.id,
            c.employee_id,
            c.salary,
            c.start_date,
            LAG(c.salary) OVER (PARTITION BY c.employee_id ORDER BY c.start_date) AS prev_salary,
            LAG(c.start_date) OVER (PARTITION BY c.employee_id ORDER BY c.start_date) AS prev_start_date
        FROM contracts c
    ),
    promotions AS (
        SELECT
            employee_id,
            start_date AS promotion_date
        FROM ordered_contracts
        WHERE prev_salary IS NOT NULL
          AND salary > prev_salary
    ),
    last_promotion AS (
        SELECT
            e.id AS employee_id,
            COALESCE(MAX(p.promotion_date), MIN(c.start_date)) AS last_promotion_date
        FROM employees e
        JOIN contracts c ON c.employee_id = e.id
        LEFT JOIN promotions p ON p.employee_id = e.id
        GROUP BY e.id
    )
    SELECT
        e.id,
        e.first_name,
        e.last_name,
        DATEDIFF(CURDATE(), lp.last_promotion_date) AS days_since_promotion,
        lp.last_promotion_date
    FROM employees e
    JOIN last_promotion lp ON lp.employee_id = e.id
    ORDER BY days_since_promotion DESC
    LIMIT ?
  `;

  const [rows] = await pool.query(query, [limit || 10]);
  const results = rows as any[];

  if (!results.length) {
    return { content: [{ type: 'text', text: 'No employees found.' }] };
  }

  const header = `# Employees Ordered by Time Since Last Promotion`;
  const tableHeader = `| Employee | Days Since Promotion | Last Promotion |\n|----------|----------------------|----------------|`;
  const tableRows = results
    .map(
      (r) =>
        `| ${r.first_name} ${r.last_name} | ${r.days_since_promotion} | ${r.last_promotion_date} |`
    )
    .join('\n');

  return {
    content: [
      { type: 'text', text: `${header}\n\n${tableHeader}\n${tableRows}` },
    ],
  };
};

export const handleGetMostRecentPromotions = async (
  limit = 5
): Promise<MCPResponse> => {
  const pool = getPool();

  const query = `
    WITH ordered_contracts AS (
        SELECT
            c.id,
            c.employee_id,
            c.salary,
            c.start_date,
            LAG(c.salary) OVER (PARTITION BY c.employee_id ORDER BY c.start_date) AS prev_salary
        FROM contracts c
    ),
    promotions AS (
        SELECT
            employee_id,
            start_date AS promotion_date
        FROM ordered_contracts
        WHERE prev_salary IS NOT NULL
          AND salary > prev_salary
    )
    SELECT
        e.id,
        e.first_name,
        e.last_name,
        p.promotion_date
    FROM employees e
    JOIN promotions p ON p.employee_id = e.id
    ORDER BY p.promotion_date DESC
    LIMIT ?
  `;

  const [rows] = await pool.query(query, [limit || 5]);
  const results = rows as any[];

  if (!results.length) {
    return { content: [{ type: 'text', text: 'No promotions found.' }] };
  }

  const header = `# Most Recent Promotions`;
  const tableHeader = `| Employee | Promotion Date |\n|----------|----------------|`;
  const tableRows = results
    .map((r) => `| ${r.first_name} ${r.last_name} | ${r.promotion_date} |`)
    .join('\n');

  return {
    content: [
      { type: 'text', text: `${header}\n\n${tableHeader}\n${tableRows}` },
    ],
  };
};

export const handleGetAveragePromotionInterval =
  async (): Promise<MCPResponse> => {
    const pool = getPool();

    const query = `
    WITH ordered_contracts AS (
        SELECT
            c.employee_id,
            c.salary,
            c.start_date,
            LAG(c.salary) OVER (PARTITION BY c.employee_id ORDER BY c.start_date) AS prev_salary,
            LAG(c.start_date) OVER (PARTITION BY c.employee_id ORDER BY c.start_date) AS prev_start_date
        FROM contracts c
    )
    SELECT
      AVG(DATEDIFF(start_date, prev_start_date)) AS avg_days_between_promotions
    FROM ordered_contracts
    WHERE prev_salary IS NOT NULL
      AND salary > prev_salary
      AND prev_start_date IS NOT NULL
  `;

    const [rows] = await pool.query(query);
    const result = (rows as any[])[0];

    if (!result || result.avg_days_between_promotions === null) {
      return {
        content: [{ type: 'text', text: 'No promotion history found.' }],
      };
    }

    return {
      content: [
        {
          type: 'text',
          text: `# Average Promotion Interval\n\n**${Math.round(
            result.avg_days_between_promotions
          )} days**`,
        },
      ],
    };
  };

export const handleGetEmployeeById = async (
  id: number
): Promise<MCPResponse> => {
  try {
    const res = await fetch(`http://localhost:8080/employees/${id}`);
    if (!res.ok) {
      throw new Error(`Failed to fetch employee with ID ${id}`);
    }
    console.log('called get by id');
    const employee = (await res.json()) as EnrichedEmployee;
    console.log(employee);
    if (!employee) {
      return {
        content: [{ type: 'text', text: `No employee found with ID ${id}.` }],
      };
    }

    const resultText = `# Employee Details

**ID**: ${employee.id}
**Name**: ${employee.firstName} ${employee.lastName}
**Date of Birth**: ${employee.dateOfBirth}
**Department**: ${employee.departmentName ?? 'N/A'}
**Job Title**: ${employee.jobTitle ?? 'N/A'}
**Salary**: ${employee.salary?.toLocaleString?.() ?? 'N/A'}`;

    return { content: [{ type: 'text', text: resultText }] };
  } catch (err: any) {
    return {
      content: [
        {
          type: 'text',
          text: `Error fetching employee with ID ${id}: ${err.message}`,
        },
      ],
    };
  }
};

export const handleSearchEmployeesByName = async (
  name: string
): Promise<MCPResponse> => {
  try {
    const res = await fetch(
      `http://localhost:8080/employees?searchBy=name&searchTerm=${encodeURIComponent(
        name
      )}`
    );
    if (!res.ok) {
      throw new Error(`Failed to search employees by name: ${name}`);
    }
    const result = (await res.json()) as PaginatedEmployeeResponse;

    if (!result || result.totalResults === 0) {
      return {
        content: [
          { type: 'text', text: `No employees found for name: ${name}` },
        ],
      };
    }

    const list = result.data
      .map(
        (e: EmployeeSummary) =>
          `- **${e.firstName} ${e.lastName}** (ID: ${e.id},
          }`
      )
      .join('\n');

    const resultText = `# Employees matching "${name}"\n
    ## Total results: ${result.totalResults}\n
    ##List\n
    \n\n${list}`;

    return { content: [{ type: 'text', text: resultText }] };
  } catch (err: any) {
    return {
      content: [
        {
          type: 'text',
          text: `Error searching employees by name: ${err.message}`,
        },
      ],
    };
  }
};

// Helper function to execute a tool by name
export const executeTool = async (
  toolName: ToolName,
  args: Record<string, any> = {}
): Promise<MCPResponse> => {
  switch (toolName as ToolName) {
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

    case 'promotion_gap':
      return await handleGetEmployeesByPromotionGap(args.limit);

    case 'avg_promotion_interval':
      return await handleGetAveragePromotionInterval();

    case 'recent_promotions':
      return await handleGetMostRecentPromotions(args.limit);
    case 'get_employee_by_id':
      if (!args.id) {
        throw new Error('id argument is required');
      }
      return handleGetEmployeeById(args.id);
    case 'search_employees_by_name':
      if (!args.name) {
        throw new Error('name argument is required');
      }
      return handleSearchEmployeesByName(args.name);

    default:
      throw new Error(`Tool '${toolName}' not found`);
  }
};
