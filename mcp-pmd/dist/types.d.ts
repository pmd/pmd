/**
 * Type definitions for PMD MCP Server
 */
export interface PmdViolation {
    file: string;
    line: number;
    column?: number;
    endLine?: number;
    endColumn?: number;
    rule: string;
    ruleset: string;
    priority: number;
    message: string;
    externalInfoUrl?: string;
}
export interface PmdResult {
    success: boolean;
    violations: PmdViolation[];
    filesAnalyzed: number;
    processingErrors: string[];
    suppressedViolations: number;
}
export interface CpdDuplication {
    lines: number;
    tokens: number;
    files: Array<{
        path: string;
        startLine: number;
        endLine: number;
    }>;
    codefragment?: string;
}
export interface CpdResult {
    success: boolean;
    duplications: CpdDuplication[];
    filesAnalyzed: number;
    processingErrors: string[];
}
export interface PmdLanguage {
    id: string;
    name: string;
    versions: string[];
    defaultVersion: string;
}
export interface PmdRuleset {
    name: string;
    description: string;
    language: string;
    rules: number;
}
export interface PmdExecutorOptions {
    /** Path to PMD executable (auto-detected if not provided) */
    pmdPath?: string;
    /** Timeout in milliseconds (default: 60000) */
    timeout?: number;
    /** Working directory for PMD execution */
    cwd?: string;
}
export type PmdOutputFormat = 'text' | 'json' | 'xml' | 'csv' | 'html';
export interface PmdCheckOptions {
    /** Path to source files or directory */
    path: string;
    /** Rulesets to apply (e.g., "rulesets/java/quickstart.xml") */
    rulesets?: string[];
    /** Specific language version (e.g., "java-21") */
    languageVersion?: string;
    /** Output format */
    format?: PmdOutputFormat;
    /** Minimum priority to report (1-5, where 1 is highest) */
    minimumPriority?: number;
    /** Files/patterns to exclude */
    excludes?: string[];
    /** Number of threads */
    threads?: number;
    /** Enable incremental analysis */
    incremental?: boolean;
}
export interface CpdCheckOptions {
    /** Path to source files or directory */
    path: string;
    /** Programming language */
    language: string;
    /** Minimum token count for duplication detection */
    minimumTokens?: number;
    /** Skip duplicate files */
    skipDuplicateFiles?: boolean;
    /** Ignore literals */
    ignoreLiterals?: boolean;
    /** Ignore identifiers */
    ignoreIdentifiers?: boolean;
    /** Files/patterns to exclude */
    excludes?: string[];
}
//# sourceMappingURL=types.d.ts.map