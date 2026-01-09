/**
 * PMD List Tools
 * 
 * List available languages and rulesets.
 */

import { pmdExecutor } from '../pmd-executor.js';

/**
 * Execute list languages tool
 */
export async function executeListLanguages(): Promise<string> {
  const pmdLanguages = await pmdExecutor.listLanguages();
  const cpdLanguages = await pmdExecutor.listCpdLanguages();

  const lines: string[] = [
    '## PMD Supported Languages\n',
    '### Static Analysis (pmd_check)',
    'Languages with PMD rules for code quality analysis:\n',
  ];

  for (const lang of pmdLanguages) {
    lines.push(`- \`${lang}\``);
  }

  lines.push('\n### Copy-Paste Detection (pmd_cpd)');
  lines.push('Languages supported for duplicate code detection:\n');

  for (const lang of cpdLanguages) {
    lines.push(`- \`${lang}\``);
  }

  lines.push('\n**Note**: Use the language ID when calling pmd_check or pmd_cpd tools.');

  return lines.join('\n');
}

/**
 * Execute list rulesets tool
 */
export async function executeListRulesets(language?: string): Promise<string> {
  // Common rulesets by language
  const rulesetsByLanguage: Record<string, Array<{ name: string; description: string }>> = {
    java: [
      { name: 'rulesets/java/quickstart.xml', description: 'Quick start ruleset with essential rules' },
      { name: 'category/java/bestpractices.xml', description: 'Best practices and coding standards' },
      { name: 'category/java/codestyle.xml', description: 'Code style and formatting rules' },
      { name: 'category/java/design.xml', description: 'Design and architecture rules' },
      { name: 'category/java/documentation.xml', description: 'Documentation and comment rules' },
      { name: 'category/java/errorprone.xml', description: 'Error-prone code patterns' },
      { name: 'category/java/multithreading.xml', description: 'Multithreading issues' },
      { name: 'category/java/performance.xml', description: 'Performance optimization rules' },
      { name: 'category/java/security.xml', description: 'Security vulnerability detection' },
    ],
    ecmascript: [
      { name: 'category/ecmascript/bestpractices.xml', description: 'JavaScript best practices' },
      { name: 'category/ecmascript/codestyle.xml', description: 'JavaScript code style' },
      { name: 'category/ecmascript/errorprone.xml', description: 'Error-prone JavaScript patterns' },
    ],
    apex: [
      { name: 'rulesets/apex/quickstart.xml', description: 'Apex quick start ruleset' },
      { name: 'category/apex/bestpractices.xml', description: 'Apex best practices' },
      { name: 'category/apex/codestyle.xml', description: 'Apex code style' },
      { name: 'category/apex/design.xml', description: 'Apex design rules' },
      { name: 'category/apex/errorprone.xml', description: 'Error-prone Apex patterns' },
      { name: 'category/apex/performance.xml', description: 'Apex performance rules' },
      { name: 'category/apex/security.xml', description: 'Apex security rules' },
    ],
    html: [
      { name: 'category/html/bestpractices.xml', description: 'HTML best practices' },
    ],
    xml: [
      { name: 'category/xml/bestpractices.xml', description: 'XML best practices' },
      { name: 'category/xml/errorprone.xml', description: 'XML error-prone patterns' },
    ],
    plsql: [
      { name: 'category/plsql/bestpractices.xml', description: 'PL/SQL best practices' },
      { name: 'category/plsql/codestyle.xml', description: 'PL/SQL code style' },
      { name: 'category/plsql/design.xml', description: 'PL/SQL design rules' },
      { name: 'category/plsql/errorprone.xml', description: 'Error-prone PL/SQL patterns' },
    ],
    kotlin: [
      { name: 'category/kotlin/bestpractices.xml', description: 'Kotlin best practices' },
    ],
    swift: [
      { name: 'category/swift/bestpractices.xml', description: 'Swift best practices' },
      { name: 'category/swift/errorprone.xml', description: 'Error-prone Swift patterns' },
    ],
  };

  const lines: string[] = ['## PMD Rulesets\n'];

  if (language) {
    const lang = language.toLowerCase();
    const rulesets = rulesetsByLanguage[lang];

    if (rulesets) {
      lines.push(`### ${language} Rulesets\n`);
      for (const rs of rulesets) {
        lines.push(`- **\`${rs.name}\`**`);
        lines.push(`  ${rs.description}\n`);
      }
    } else {
      lines.push(`No predefined rulesets found for "${language}".`);
      lines.push('\nYou can still use category-based rulesets like:');
      lines.push(`- \`category/${lang}/bestpractices.xml\``);
      lines.push(`- \`category/${lang}/errorprone.xml\``);
    }
  } else {
    lines.push('Available rulesets by language:\n');
    for (const [lang, rulesets] of Object.entries(rulesetsByLanguage)) {
      lines.push(`### ${lang}\n`);
      for (const rs of rulesets) {
        lines.push(`- \`${rs.name}\` - ${rs.description}`);
      }
      lines.push('');
    }
  }

  lines.push('\n**Usage**: Pass ruleset names to the `rulesets` parameter of `pmd_check`.');

  return lines.join('\n');
}

/**
 * Tool definitions for MCP server
 */
export const pmdListLanguagesTool = {
  name: 'pmd_list_languages',
  description:
    'List all programming languages supported by PMD for static analysis and copy-paste detection.',
  inputSchema: {
    type: 'object' as const,
    properties: {},
    required: [],
  },
};

export const pmdListRulesetsTool = {
  name: 'pmd_list_rulesets',
  description:
    'List available PMD rulesets for code analysis. Optionally filter by language.',
  inputSchema: {
    type: 'object' as const,
    properties: {
      language: {
        type: 'string',
        description:
          'Filter rulesets by language (e.g., "java", "ecmascript", "apex"). If not specified, shows all languages.',
      },
    },
    required: [],
  },
};
