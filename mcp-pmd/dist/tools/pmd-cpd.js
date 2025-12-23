"use strict";
/**
 * PMD CPD (Copy-Paste Detector) Tool
 *
 * Detects duplicated code across source files.
 */
Object.defineProperty(exports, "__esModule", { value: true });
exports.pmdCpdTool = exports.pmdCpdSchema = void 0;
exports.executePmdCpd = executePmdCpd;
const zod_1 = require("zod");
const pmd_executor_js_1 = require("../pmd-executor.js");
// Input schema for pmd_cpd tool
exports.pmdCpdSchema = zod_1.z.object({
    path: zod_1.z.string().describe('Path to source file or directory to analyze'),
    language: zod_1.z
        .string()
        .describe('Programming language (e.g., "python", "java", "typescript", "javascript")'),
    minimum_tokens: zod_1.z
        .number()
        .optional()
        .default(50)
        .describe('Minimum token count for duplication detection. Default: 50'),
    ignore_literals: zod_1.z
        .boolean()
        .optional()
        .describe('Ignore literal values when detecting duplicates'),
    ignore_identifiers: zod_1.z
        .boolean()
        .optional()
        .describe('Ignore identifier names when detecting duplicates'),
    excludes: zod_1.z
        .array(zod_1.z.string())
        .optional()
        .describe('File patterns to exclude from analysis'),
});
/**
 * Format a single duplication for display
 */
function formatDuplication(dup, index) {
    const lines = [];
    lines.push(`#### Duplication #${index + 1}`);
    lines.push(`**${dup.lines} lines** (${dup.tokens} tokens) duplicated in ${dup.files.length} locations:\n`);
    for (const file of dup.files) {
        lines.push(`- \`${file.path}\` lines ${file.startLine}-${file.endLine}`);
    }
    if (dup.codefragment) {
        // Truncate long code fragments
        const fragment = dup.codefragment.length > 500
            ? dup.codefragment.substring(0, 500) + '\n... (truncated)'
            : dup.codefragment;
        lines.push('\n```');
        lines.push(fragment);
        lines.push('```');
    }
    return lines.join('\n');
}
/**
 * Format CPD result for LLM consumption
 */
function formatResult(result, language) {
    const parts = ['## CPD (Copy-Paste Detection) Results\n'];
    // Summary
    parts.push(`**Language**: ${language}`);
    parts.push(`**Files Analyzed**: ${result.filesAnalyzed}`);
    parts.push(`**Duplications Found**: ${result.duplications.length}`);
    parts.push('');
    // Errors
    if (result.processingErrors.length > 0) {
        parts.push('### ⚠️ Processing Errors\n');
        for (const err of result.processingErrors) {
            parts.push(`- ${err}`);
        }
        parts.push('');
    }
    // Duplications
    if (result.duplications.length > 0) {
        // Sort by size (lines * occurrences)
        const sorted = [...result.duplications].sort((a, b) => (b.lines * b.files.length) - (a.lines * a.files.length));
        // Calculate total duplicated lines
        const totalDuplicatedLines = sorted.reduce((sum, d) => sum + d.lines * (d.files.length - 1), 0);
        parts.push(`**Total Duplicated Lines**: ~${totalDuplicatedLines}\n`);
        parts.push('### Duplications\n');
        // Show top 10 duplications
        const toShow = sorted.slice(0, 10);
        for (let i = 0; i < toShow.length; i++) {
            parts.push(formatDuplication(toShow[i], i));
            parts.push('');
        }
        if (sorted.length > 10) {
            parts.push(`\n*... and ${sorted.length - 10} more duplications*`);
        }
    }
    else if (result.success) {
        parts.push('✅ **No duplications found!** The code has no significant copy-paste issues.');
    }
    return parts.join('\n');
}
/**
 * Execute CPD tool
 */
async function executePmdCpd(input) {
    const result = await pmd_executor_js_1.pmdExecutor.cpd({
        path: input.path,
        language: input.language,
        minimumTokens: input.minimum_tokens,
        ignoreLiterals: input.ignore_literals,
        ignoreIdentifiers: input.ignore_identifiers,
        excludes: input.excludes,
    });
    if (!result.success && result.processingErrors.length > 0) {
        return `## CPD Analysis Failed\n\n${result.processingErrors.join('\n')}`;
    }
    return formatResult(result, input.language);
}
/**
 * Tool definition for MCP server
 */
exports.pmdCpdTool = {
    name: 'pmd_cpd',
    description: 'Run CPD (Copy-Paste Detector) to find duplicated code. Supports many languages including Python, Java, JavaScript, TypeScript, Go, Ruby, C/C++, and more.',
    inputSchema: {
        type: 'object',
        properties: {
            path: {
                type: 'string',
                description: 'Path to source file or directory to analyze',
            },
            language: {
                type: 'string',
                description: 'Programming language. Supported: python, java, javascript, typescript, kotlin, swift, go, ruby, cpp, cs, php, rust, scala, and more.',
            },
            minimum_tokens: {
                type: 'number',
                description: 'Minimum token count for duplication detection. Lower values find smaller duplications. Default: 50',
            },
            ignore_literals: {
                type: 'boolean',
                description: 'Ignore literal values (strings, numbers) when detecting duplicates. Useful for finding structural duplications.',
            },
            ignore_identifiers: {
                type: 'boolean',
                description: 'Ignore identifier names when detecting duplicates. Finds duplicates even when variable names differ.',
            },
            excludes: {
                type: 'array',
                items: { type: 'string' },
                description: 'File patterns to exclude (e.g., ["**/test/**", "**/vendor/**"])',
            },
        },
        required: ['path', 'language'],
    },
};
//# sourceMappingURL=pmd-cpd.js.map