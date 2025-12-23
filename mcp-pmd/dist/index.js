"use strict";
/**
 * PMD MCP Server
 *
 * A Model Context Protocol server that provides PMD static code analysis
 * capabilities to AI assistants.
 *
 * Transport: stdio (like Playwright MCP)
 */
Object.defineProperty(exports, "__esModule", { value: true });
const mcp_js_1 = require("@modelcontextprotocol/sdk/server/mcp.js");
const stdio_js_1 = require("@modelcontextprotocol/sdk/server/stdio.js");
const zod_1 = require("zod");
const pmd_executor_js_1 = require("./pmd-executor.js");
const pmd_check_js_1 = require("./tools/pmd-check.js");
const pmd_cpd_js_1 = require("./tools/pmd-cpd.js");
const pmd_list_js_1 = require("./tools/pmd-list.js");
// Log to stderr to avoid corrupting stdio transport
const log = {
    info: (msg) => console.error(`[pmd-mcp] ${msg}`),
    error: (msg) => console.error(`[pmd-mcp] ERROR: ${msg}`),
};
/**
 * Create and configure the MCP server
 */
async function createServer() {
    // Initialize PMD executor
    const pmdAvailable = await pmd_executor_js_1.pmdExecutor.initialize();
    if (!pmdAvailable) {
        log.error('PMD not found. Some tools may not work.');
        log.error('Install PMD via: brew install pmd (macOS) or download from https://pmd.github.io/');
    }
    else {
        const version = await pmd_executor_js_1.pmdExecutor.getVersion();
        log.info(`PMD ${version} detected`);
    }
    // Create MCP server
    const server = new mcp_js_1.McpServer({
        name: 'pmd',
        version: '0.1.0',
    });
    // Register pmd_check tool
    server.tool('pmd_check', 'Run PMD static code analysis on source files. Detects code quality issues, bugs, and style violations.', pmd_check_js_1.pmdCheckSchema.shape, async (params) => {
        const input = params;
        log.info(`Executing pmd_check on: ${input.path}`);
        try {
            const result = await (0, pmd_check_js_1.executePmdCheck)(input);
            return {
                content: [{ type: 'text', text: result }],
            };
        }
        catch (err) {
            const message = err instanceof Error ? err.message : String(err);
            log.error(`pmd_check failed: ${message}`);
            return {
                content: [{ type: 'text', text: `Error: ${message}` }],
                isError: true,
            };
        }
    });
    // Register pmd_cpd tool
    server.tool('pmd_cpd', 'Run CPD (Copy-Paste Detector) to find duplicated code. Supports Python, Java, JavaScript, TypeScript, and more.', pmd_cpd_js_1.pmdCpdSchema.shape, async (params) => {
        const input = params;
        log.info(`Executing pmd_cpd on: ${input.path} (${input.language})`);
        try {
            const result = await (0, pmd_cpd_js_1.executePmdCpd)(input);
            return {
                content: [{ type: 'text', text: result }],
            };
        }
        catch (err) {
            const message = err instanceof Error ? err.message : String(err);
            log.error(`pmd_cpd failed: ${message}`);
            return {
                content: [{ type: 'text', text: `Error: ${message}` }],
                isError: true,
            };
        }
    });
    // Register pmd_list_languages tool
    server.tool('pmd_list_languages', 'List all programming languages supported by PMD for static analysis and copy-paste detection.', {}, async () => {
        log.info('Executing pmd_list_languages');
        try {
            const result = await (0, pmd_list_js_1.executeListLanguages)();
            return {
                content: [{ type: 'text', text: result }],
            };
        }
        catch (err) {
            const message = err instanceof Error ? err.message : String(err);
            return {
                content: [{ type: 'text', text: `Error: ${message}` }],
                isError: true,
            };
        }
    });
    // Register pmd_list_rulesets tool
    server.tool('pmd_list_rulesets', 'List available PMD rulesets for code analysis. Optionally filter by language.', {
        language: zod_1.z.string().optional().describe('Filter rulesets by language (e.g., "java", "ecmascript")'),
    }, async (params) => {
        const language = params.language;
        log.info(`Executing pmd_list_rulesets${language ? ` for ${language}` : ''}`);
        try {
            const result = await (0, pmd_list_js_1.executeListRulesets)(language);
            return {
                content: [{ type: 'text', text: result }],
            };
        }
        catch (err) {
            const message = err instanceof Error ? err.message : String(err);
            return {
                content: [{ type: 'text', text: `Error: ${message}` }],
                isError: true,
            };
        }
    });
    return server;
}
/**
 * Main entry point
 */
async function main() {
    log.info('Starting PMD MCP Server...');
    try {
        const server = await createServer();
        const transport = new stdio_js_1.StdioServerTransport();
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
    }
    catch (err) {
        log.error(`Failed to start server: ${err instanceof Error ? err.message : String(err)}`);
        process.exit(1);
    }
}
// Run the server
main();
//# sourceMappingURL=index.js.map