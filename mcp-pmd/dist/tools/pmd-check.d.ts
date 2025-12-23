/**
 * PMD Check Tool
 *
 * Runs PMD static code analysis on source files.
 */
import { z } from 'zod';
export declare const pmdCheckSchema: z.ZodObject<{
    path: z.ZodString;
    rulesets: z.ZodOptional<z.ZodArray<z.ZodString, "many">>;
    language_version: z.ZodOptional<z.ZodString>;
    minimum_priority: z.ZodOptional<z.ZodNumber>;
    excludes: z.ZodOptional<z.ZodArray<z.ZodString, "many">>;
}, "strip", z.ZodTypeAny, {
    path: string;
    rulesets?: string[] | undefined;
    language_version?: string | undefined;
    minimum_priority?: number | undefined;
    excludes?: string[] | undefined;
}, {
    path: string;
    rulesets?: string[] | undefined;
    language_version?: string | undefined;
    minimum_priority?: number | undefined;
    excludes?: string[] | undefined;
}>;
export type PmdCheckInput = z.infer<typeof pmdCheckSchema>;
/**
 * Execute PMD check tool
 */
export declare function executePmdCheck(input: PmdCheckInput): Promise<string>;
/**
 * Tool definition for MCP server
 */
export declare const pmdCheckTool: {
    name: string;
    description: string;
    inputSchema: {
        type: "object";
        properties: {
            path: {
                type: string;
                description: string;
            };
            rulesets: {
                type: string;
                items: {
                    type: string;
                };
                description: string;
            };
            language_version: {
                type: string;
                description: string;
            };
            minimum_priority: {
                type: string;
                description: string;
            };
            excludes: {
                type: string;
                items: {
                    type: string;
                };
                description: string;
            };
        };
        required: string[];
    };
};
//# sourceMappingURL=pmd-check.d.ts.map