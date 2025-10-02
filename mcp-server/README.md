# MySQL MCP Server

An MCP (Model Context Protocol) server for MySQL database access via HTTP with integrated LLM chat capabilities.

## Setup

1. Install dependencies:

```bash
npm install
```

2. Create a `.env` file:

```
DB_HOST=localhost
DB_USER=root
DB_PASSWORD=your_password
DB_NAME=your_database
DB_PORT=3306
PORT=3000
ANTHROPIC_API_KEY=your_anthropic_api_key_here
```

3. Build and run:

```bash
npm run build
npm start
```

Or for development:

```bash
npm run dev
```

## API Endpoints

### Health Check

```bash
GET http://localhost:3000/health
```

### List Tools

```bash
POST http://localhost:3000/tools/list
```

### Execute Tool

```bash
POST http://localhost:3000/tools/call
Content-Type: application/json

{
  "method": "tools/call",
  "params": {
    "name": "get_glossary",
    "arguments": {}
  }
}
```

### Chat with LLM (NEW!)

```bash
POST http://localhost:3000/chat
Content-Type: application/json

{
  "message": "Who is the highest paid employee?",
  "conversation_history": []
}
```

## Available Tools

1. **get_glossary** - Returns business term definitions
2. **show_tables** - Lists all database tables
3. **describe_table** - Shows table structure
4. **run_query** - Executes SELECT queries
5. **get_highest_paid_employee** - Returns highest paid employee info

## Example Requests

### Chat Examples

**Simple Question:**

```bash
curl -X POST http://localhost:3000/chat \
  -H "Content-Type: application/json" \
  -d '{
    "message": "Who is the highest paid employee?"
  }'
```

**Complex Query:**

```bash
curl -X POST http://localhost:3000/chat \
  -H "Content-Type: application/json" \
  -d '{
    "message": "Show me all employees in the Engineering department with salaries over 100k"
  }'
```

**With Conversation History:**

```bash
curl -X POST http://localhost:3000/chat \
  -H "Content-Type: application/json" \
  -d '{
    "message": "What about their average tenure?",
    "conversation_history": [
      {
        "role": "user",
        "content": "Show me employees in Engineering"
      },
      {
        "role": "assistant",
        "content": "Here are the employees in the Engineering department..."
      }
    ]
  }'
```

**Response Format:**

```json
{
  "response": "The highest paid employee is John Doe, who works as a Senior Software Engineer in the Engineering department with a salary of $150,000.",
  "tools_used": ["get_highest_paid_employee"]
}
```

### Direct Tool Calls

**Get Glossary:**

```bash
curl -X POST http://localhost:3000/tools/call \
  -H "Content-Type: application/json" \
  -d '{
    "method": "tools/call",
    "params": {
      "name": "get_glossary"
    }
  }'
```

**Describe Table:**

```bash
curl -X POST http://localhost:3000/tools/call \
  -H "Content-Type: application/json" \
  -d '{
    "method": "tools/call",
    "params": {
      "name": "describe_table",
      "arguments": {
        "table_name": "employees"
      }
    }
  }'
```

**Run Query:**

```bash
curl -X POST http://localhost:3000/tools/call \
  -H "Content-Type: application/json" \
  -d '{
    "method": "tools/call",
    "params": {
      "name": "run_query",
      "arguments": {
        "query": "SELECT * FROM employees LIMIT 5"
      }
    }
  }'
```

## How the Chat Endpoint Works

The `/chat` endpoint uses Claude (Anthropic's LLM) with tool calling:

1. User sends a natural language message
2. Claude analyzes the message and decides which tools to use
3. The server executes the necessary database queries
4. Claude receives the results and formats them into a human-readable response
5. The response and list of tools used are returned to the user

This allows your chatbot to:

- Understand natural language queries
- Automatically select the right database operations
- Handle complex multi-step queries
- Maintain conversation context with history

````

### Execute Tool
```bash
POST http://localhost:3000/tools/call
Content-Type: application/json

{
  "method": "tools/call",
  "params": {
    "name": "get_glossary",
    "arguments": {}
  }
}
````

## Available Tools

1. **get_glossary** - Returns business term definitions
2. **show_tables** - Lists all database tables
3. **describe_table** - Shows table structure
4. **run_query** - Executes SELECT queries
5. **get_highest_paid_employee** - Returns highest paid employee info

## Example Requests

### Get Glossary

```bash
curl -X POST http://localhost:3000/tools/call \
  -H "Content-Type: application/json" \
  -d '{
    "method": "tools/call",
    "params": {
      "name": "get_glossary"
    }
  }'
```

### Describe Table

```bash
curl -X POST http://localhost:3000/tools/call \
  -H "Content-Type: application/json" \
  -d '{
    "method": "tools/call",
    "params": {
      "name": "describe_table",
      "arguments": {
        "table_name": "employees"
      }
    }
  }'
```

### Run Query

```bash
curl -X POST http://localhost:3000/tools/call \
  -H "Content-Type: application/json" \
  -d '{
    "method": "tools/call",
    "params": {
      "name": "run_query",
      "arguments": {
        "query": "SELECT * FROM employees LIMIT 5"
      }
    }
  }'
```
