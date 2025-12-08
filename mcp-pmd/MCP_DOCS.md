# MCP (Model Context Protocol) Documentation Reference

## Overview

The Model Context Protocol (MCP) is a standard that allows AI applications to provide context for LLMs in a standardized way. This document summarizes the key concepts needed to build an MCP server for PMD.

## Core Concepts

### 1. Server Types

MCP servers can provide three main capabilities:

1. **Tools** - Functions that can be called by the LLM (with user approval)
2. **Resources** - File-like data that can be read by clients (like API responses or file contents)
3. **Prompts** - Pre-written templates that help users accomplish specific tasks

For PMD MCP, we'll primarily focus on **Tools**.

### 2. Transport Mechanisms

MCP supports two standard transports:

1. **stdio** - Communication over standard input/output (recommended for local tools like Playwright)
2. **Streamable HTTP** - For remote servers

We'll use **stdio** transport for PMD MCP (like Playwright does).

### 3. Tool Definition

Tools are defined with:
- `name` - Unique identifier
- `description` - What the tool does
- `inputSchema` - JSON Schema for parameters

Example:
```typescript
{
  name: "pmd_check",
  description: "Run PMD static code analysis on source files",
  inputSchema: {
    type: "object",
    properties: {
      path: { type: "string", description: "Path to source files or directory" },
      language: { type: "string", description: "Programming language (java, javascript, etc.)" },
      rulesets: { type: "array", items: { type: "string" }, description: "Rulesets to apply" }
    },
    required: ["path"]
  }
}
```

## TypeScript SDK

### Installation
```bash
npm install @modelcontextprotocol/sdk zod
```

### Basic Server Structure
```typescript
import { McpServer } from "@modelcontextprotocol/sdk/server/mcp.js";
import { StdioServerTransport } from "@modelcontextprotocol/sdk/server/stdio.js";

const server = new McpServer({
  name: "pmd",
  version: "1.0.0"
});

// Register tools
server.tool("pmd_check", { /* schema */ }, async (params) => {
  // Implementation
});

// Connect via stdio
const transport = new StdioServerTransport();
await server.connect(transport);
```

## Key Implementation Notes

### Logging (CRITICAL for stdio)
- **NEVER** write to stdout (breaks JSON-RPC messages)
- Use stderr or file-based logging
- No `console.log()` - use `console.error()` or a logging library

### Configuration Pattern (like Playwright)
```json
{
  "mcpServers": {
    "pmd": {
      "command": "npx",
      "args": ["@pmd/mcp@latest"]
    }
  }
}
```

## References

- TypeScript SDK: https://github.com/modelcontextprotocol/typescript-sdk
- MCP Specification: https://modelcontextprotocol.io/specification/2025-06-18
- Playwright MCP (reference): https://github.com/microsoft/playwright-mcp
- MCP Servers Registry: https://github.com/modelcontextprotocol/servers
