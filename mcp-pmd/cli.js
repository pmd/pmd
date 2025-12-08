#!/usr/bin/env node

/**
 * PMD MCP Server CLI
 * 
 * Usage:
 *   npx pmd-mcp          - Start the MCP server (stdio transport)
 *   npx pmd-mcp --help   - Show help
 *   npx pmd-mcp --version - Show version
 */

const args = process.argv.slice(2);

if (args.includes('--help') || args.includes('-h')) {
  console.error(`
PMD MCP Server - Static code analysis for AI assistants

Usage:
  npx pmd-mcp              Start the MCP server (stdio transport)
  npx pmd-mcp --help       Show this help message
  npx pmd-mcp --version    Show version

Configuration (add to your MCP client config):
  {
    "mcpServers": {
      "pmd": {
        "command": "npx",
        "args": ["pmd-mcp"]
      }
    }
  }

Requirements:
  - Node.js 18+
  - PMD installed (via Homebrew, or in PATH)

Tools provided:
  - pmd_check        Run PMD static analysis on source files
  - pmd_cpd          Run copy-paste detection (CPD)
  - pmd_list_rules   List available rules for a language

For more information: https://github.com/pmd/pmd
`);
  process.exit(0);
}

if (args.includes('--version') || args.includes('-v')) {
  const pkg = require('./package.json');
  console.error(`@pmd/mcp v${pkg.version}`);
  process.exit(0);
}

// Start the MCP server
import('./dist/index.js').catch((err) => {
  console.error('Failed to start PMD MCP server:', err.message);
  console.error('Make sure to run "npm run build" first, or use "npm run dev" for development.');
  process.exit(1);
});
