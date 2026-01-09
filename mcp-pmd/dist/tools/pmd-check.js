"use strict";
/**
 * PMD Check Tool
 *
 * Runs PMD static code analysis on source files.
 */
Object.defineProperty(exports, "__esModule", { value: true });
exports.pmdCheckTool = exports.pmdCheckSchema = void 0;
exports.executePmdCheck = executePmdCheck;
const zod_1 = require("zod");
const pmd_executor_js_1 = require("../pmd-executor.js");
// Input schema for pmd_check tool
exports.pmdCheckSchema = zod_1.z.object({
    path: zod_1.z.string().describe('Path to source file or directory to analyze'),
    rulesets: zod_1.z
        .array(zod_1.z.string())
        .optional()
        .describe('Rulesets to apply (e.g., ["rulesets/java/quickstart.xml"]). Defaults to quickstart.'),
    language_version: zod_1.z
        .string()
        .optional()
        .describe('Language version (e.g., "java-21", "ecmascript-ES2022")'),
    minimum_priority: zod_1.z
        .number()
        .min(1)
        .max(5)
        .optional()
        .describe('Minimum priority to report (1=highest, 5=lowest). Default: all'),
    excludes: zod_1.z
        .array(zod_1.z.string())
        .optional()
        .describe('File patterns to exclude from analysis'),
});
/**
 * Format violations by priority for LLM consumption
 */
function formatViolations(violations) {
    if (violations.length === 0) {
        return 'No violations found.';
    }
    // Group by priority
    const byPriority = {};
    for (const v of violations) {
        if (!byPriority[v.priority]) {
            byPriority[v.priority] = [];
        }
        byPriority[v.priority].push(v);
    }
    const priorityLabels = {
        1: '🔴 Critical (Priority 1)',
        2: '🟠 High (Priority 2)',
        3: '🟡 Medium (Priority 3)',
        4: '🔵 Low (Priority 4)',
        5: '⚪ Info (Priority 5)',
    };
    const sections = [];
    for (const priority of [1, 2, 3, 4, 5]) {
        const items = byPriority[priority];
        if (!items || items.length === 0)
            continue;
        const label = priorityLabels[priority];
        const lines = items.map((v) => {
            const location = v.column
                ? `${v.file}:${v.line}:${v.column}`
                : `${v.file}:${v.line}`;
            return `- \`${location}\` **${v.rule}**: ${v.message}`;
        });
        sections.push(`### ${label} (${items.length})\n\n${lines.join('\n')}`);
    }
    return sections.join('\n\n');
}
/**
 * Format PMD result for LLM consumption
 */
function formatResult(result) {
    const parts = ['## PMD Analysis Results\n'];
    // Summary
    parts.push(`**Files Analyzed**: ${result.filesAnalyzed}`);
    parts.push(`**Violations Found**: ${result.violations.length}`);
    if (result.suppressedViolations > 0) {
        parts.push(`**Suppressed**: ${result.suppressedViolations}`);
    }
    parts.push('');
    // Errors
    if (result.processingErrors.length > 0) {
        parts.push('### ⚠️ Processing Errors\n');
        for (const err of result.processingErrors) {
            parts.push(`- ${err}`);
        }
        parts.push('');
    }
    // Violations
    if (result.violations.length > 0) {
        parts.push(formatViolations(result.violations));
    }
    else if (result.success) {
        parts.push('✅ **No violations found!** The code passes all PMD checks.');
    }
    return parts.join('\n');
}
/**
 * Execute PMD check tool
 */
async function executePmdCheck(input) {
    const result = await pmd_executor_js_1.pmdExecutor.check({
        path: input.path,
        rulesets: input.rulesets,
        languageVersion: input.language_version,
        minimumPriority: input.minimum_priority,
        excludes: input.excludes,
    });
    if (!result.success && result.processingErrors.length > 0) {
        return `## PMD Analysis Failed\n\n${result.processingErrors.join('\n')}`;
    }
    return formatResult(result);
}
/**
 * Tool definition for MCP server
 */
exports.pmdCheckTool = {
    name: 'pmd_check',
    description: 'Run PMD static code analysis on source files. Detects code quality issues, bugs, and style violations. Supports Java, JavaScript, Apex, Kotlin, Swift, and more.',
    inputSchema: {
        type: 'object',
        properties: {
            path: {
                type: 'string',
                description: 'Path to source file or directory to analyze',
            },
            rulesets: {
                type: 'array',
                items: { type: 'string' },
                description: 'Rulesets to apply (e.g., ["rulesets/java/quickstart.xml", "category/java/bestpractices.xml"]). Defaults to quickstart ruleset.',
            },
            language_version: {
                type: 'string',
                description: 'Language version (e.g., "java-21", "ecmascript-ES2022"). Auto-detected if not specified.',
            },
            minimum_priority: {
                type: 'number',
                description: 'Minimum priority to report (1=highest/critical, 5=lowest/info). Default: all priorities.',
            },
            excludes: {
                type: 'array',
                items: { type: 'string' },
                description: 'File patterns to exclude from analysis (e.g., ["**/test/**", "**/*.generated.java"])',
            },
        },
        required: ['path'],
    },
};
//# sourceMappingURL=pmd-check.js.map