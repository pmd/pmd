/**
 * PMD MCP Server
 * 
 * A Model Context Protocol server that provides PMD static code analysis
 * capabilities to AI assistants.
 * 
 * Transport: stdio (like Playwright MCP)
 */

import { McpServer } from '@modelcontextprotocol/sdk/server/mcp.js';
import { StdioServerTransport } from '@modelcontextprotocol/sdk/server/stdio.js';
import { z } from 'zod';

import { pmdExecutor } from './pmd-executor.js';
import { pmdCheckSchema, executePmdCheck, type PmdCheckInput } from './tools/pmd-check.js';
import { pmdCpdSchema, executePmdCpd, type PmdCpdInput } from './tools/pmd-cpd.js';
import {
  executeListLanguages,
  executeListRulesets,
} from './tools/pmd-list.js';

// Log to stderr to avoid corrupting stdio transport
const log = {
  info: (msg: string) => console.error(`[pmd-mcp] ${msg}`),
  error: (msg: string) => console.error(`[pmd-mcp] ERROR: ${msg}`),
};

/**
 * Create and configure the MCP server
 */
async function createServer(): Promise<McpServer> {
  // Initialize PMD executor
  const pmdAvailable = await pmdExecutor.initialize();
  if (!pmdAvailable) {
    log.error('PMD not found. Some tools may not work.');
    log.error('Install PMD via: brew install pmd (macOS) or download from https://pmd.github.io/');
  } else {
    const version = await pmdExecutor.getVersion();
    log.info(`PMD ${version} detected`);
  }

  // Create MCP server
  const server = new McpServer({
    name: 'pmd',
    version: '0.1.0',
  });

  // Register pmd_check tool
  server.tool(
    'pmd_check',
    'Run PMD static code analysis on source files. Detects code quality issues, bugs, and style violations.',
    pmdCheckSchema.shape,
    async (params) => {
      const input = params as PmdCheckInput;
      log.info(`Executing pmd_check on: ${input.path}`);
      try {
        const result = await executePmdCheck(input);
        return {
          content: [{ type: 'text', text: result }],
        };
      } catch (err) {
        const message = err instanceof Error ? err.message : String(err);
        log.error(`pmd_check failed: ${message}`);
        return {
          content: [{ type: 'text', text: `Error: ${message}` }],
          isError: true,
        };
      }
    }
  );

  // Register pmd_cpd tool
  server.tool(
    'pmd_cpd',
    'Run CPD (Copy-Paste Detector) to find duplicated code. Supports Python, Java, JavaScript, TypeScript, and more.',
    pmdCpdSchema.shape,
    async (params) => {
      const input = params as PmdCpdInput;
      log.info(`Executing pmd_cpd on: ${input.path} (${input.language})`);
      try {
        const result = await executePmdCpd(input);
        return {
          content: [{ type: 'text', text: result }],
        };
      } catch (err) {
        const message = err instanceof Error ? err.message : String(err);
        log.error(`pmd_cpd failed: ${message}`);
        return {
          content: [{ type: 'text', text: `Error: ${message}` }],
          isError: true,
        };
      }
    }
  );

  // Register pmd_list_languages tool
  server.tool(
    'pmd_list_languages',
    'List all programming languages supported by PMD for static analysis and copy-paste detection.',
    {},
    async () => {
      log.info('Executing pmd_list_languages');
      try {
        const result = await executeListLanguages();
        return {
          content: [{ type: 'text', text: result }],
        };
      } catch (err) {
        const message = err instanceof Error ? err.message : String(err);
        return {
          content: [{ type: 'text', text: `Error: ${message}` }],
          isError: true,
        };
      }
    }
  );

  // Register pmd_list_rulesets tool
  server.tool(
    'pmd_list_rulesets',
    'List available PMD rulesets for code analysis. Optionally filter by language.',
    {
      language: z.string().optional().describe('Filter rulesets by language (e.g., "java", "ecmascript")'),
    },
    async (params) => {
      const language = (params as { language?: string }).language;
      log.info(`Executing pmd_list_rulesets${language ? ` for ${language}` : ''}`);
      try {
        const result = await executeListRulesets(language);
        return {
          content: [{ type: 'text', text: result }],
        };
      } catch (err) {
        const message = err instanceof Error ? err.message : String(err);
        return {
          content: [{ type: 'text', text: `Error: ${message}` }],
          isError: true,
        };
      }
    }
  );

  return server;
}

/**
 * Main entry point
 */
async function main(): Promise<void> {
  log.info('Starting PMD MCP Server...');

  try {
    const server = await createServer();
    const transport = new StdioServerTransport();

    await server.connect(transport);
    log.info('PMD MCP Server connected via stdio');

    // Handle graceful shutdown
    process.on('SIGINT', async () => {
      log.info('Shutting down...');
      await server.close();
      process.exit(0);
    });

    process.on('SIGTERM', async () => {
      log.info('Shutting down...');
      await server.close();
      process.exit(0);
    });
  } catch (err) {
    log.error(`Failed to start server: ${err instanceof Error ? err.message : String(err)}`);
    process.exit(1);
  }
}

// Run the server
main();
