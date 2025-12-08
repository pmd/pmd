/**
 * PMD CPD (Copy-Paste Detector) Tool
 *
 * Detects duplicated code across source files.
 */
import { z } from 'zod';
export declare const pmdCpdSchema: z.ZodObject<{
    path: z.ZodString;
    language: z.ZodString;
    minimum_tokens: z.ZodDefault<z.ZodOptional<z.ZodNumber>>;
    ignore_literals: z.ZodOptional<z.ZodBoolean>;
    ignore_identifiers: z.ZodOptional<z.ZodBoolean>;
    excludes: z.ZodOptional<z.ZodArray<z.ZodString, "many">>;
}, "strip", z.ZodTypeAny, {
    path: string;
    language: string;
    minimum_tokens: number;
    excludes?: string[] | undefined;
    ignore_literals?: boolean | undefined;
    ignore_identifiers?: boolean | undefined;
}, {
    path: string;
    language: string;
    excludes?: string[] | undefined;
    minimum_tokens?: number | undefined;
    ignore_literals?: boolean | undefined;
    ignore_identifiers?: boolean | undefined;
}>;
export type PmdCpdInput = z.infer<typeof pmdCpdSchema>;
/**
 * Execute CPD tool
 */
export declare function executePmdCpd(input: PmdCpdInput): Promise<string>;
/**
 * Tool definition for MCP server
 */
export declare const pmdCpdTool: {
    name: string;
    description: string;
    inputSchema: {
        type: "object";
        properties: {
            path: {
                type: string;
                description: string;
            };
            language: {
                type: string;
                description: string;
            };
            minimum_tokens: {
                type: string;
                description: string;
            };
            ignore_literals: {
                type: string;
                description: string;
            };
            ignore_identifiers: {
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
//# sourceMappingURL=pmd-cpd.d.ts.map