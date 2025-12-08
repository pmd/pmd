/**
 * PMD CLI Executor
 *
 * Handles detection and execution of PMD command-line tool.
 */
import type { PmdResult, CpdResult, PmdCheckOptions, CpdCheckOptions, PmdExecutorOptions } from './types.js';
/**
 * Detect PMD installation path
 */
export declare function detectPmdPath(): Promise<string | null>;
/**
 * PMD Executor class
 */
export declare class PmdExecutor {
    private pmdPath;
    private options;
    constructor(options?: PmdExecutorOptions);
    /**
     * Initialize and detect PMD
     */
    initialize(): Promise<boolean>;
    /**
     * Get PMD version
     */
    getVersion(): Promise<string | null>;
    /**
     * Run PMD check analysis
     */
    check(options: PmdCheckOptions): Promise<PmdResult>;
    /**
     * Run CPD (Copy-Paste Detector)
     */
    cpd(options: CpdCheckOptions): Promise<CpdResult>;
    /**
     * Parse PMD JSON output
     */
    private parseJsonOutput;
    /**
     * Parse CPD XML output
     */
    private parseCpdXmlOutput;
    /**
     * List supported languages
     */
    listLanguages(): Promise<string[]>;
    /**
     * List CPD supported languages
     */
    listCpdLanguages(): Promise<string[]>;
}
export declare const pmdExecutor: PmdExecutor;
//# sourceMappingURL=pmd-executor.d.ts.map