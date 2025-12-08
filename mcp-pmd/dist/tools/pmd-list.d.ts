/**
 * PMD List Tools
 *
 * List available languages and rulesets.
 */
/**
 * Execute list languages tool
 */
export declare function executeListLanguages(): Promise<string>;
/**
 * Execute list rulesets tool
 */
export declare function executeListRulesets(language?: string): Promise<string>;
/**
 * Tool definitions for MCP server
 */
export declare const pmdListLanguagesTool: {
    name: string;
    description: string;
    inputSchema: {
        type: "object";
        properties: {};
        required: never[];
    };
};
export declare const pmdListRulesetsTool: {
    name: string;
    description: string;
    inputSchema: {
        type: "object";
        properties: {
            language: {
                type: string;
                description: string;
            };
        };
        required: never[];
    };
};
//# sourceMappingURL=pmd-list.d.ts.map