/**
 * PMD CLI Executor
 * 
 * Handles detection and execution of PMD command-line tool.
 */

import { spawn } from 'child_process';
import { existsSync } from 'fs';
import type {
  PmdResult,
  PmdViolation,
  CpdResult,
  CpdDuplication,
  PmdCheckOptions,
  CpdCheckOptions,
  PmdExecutorOptions,
} from './types.js';

// Log to stderr to avoid corrupting stdio transport
const log = {
  info: (msg: string) => console.error(`[pmd-mcp] ${msg}`),
  error: (msg: string) => console.error(`[pmd-mcp] ERROR: ${msg}`),
  debug: (msg: string) => {
    if (process.env.DEBUG) console.error(`[pmd-mcp] DEBUG: ${msg}`);
  },
};

/**
 * Detect PMD installation path
 */
export async function detectPmdPath(): Promise<string | null> {
  // Common PMD locations
  const candidates = [
    'pmd', // In PATH (homebrew, etc.)
    '/usr/local/bin/pmd',
    '/opt/homebrew/bin/pmd',
    process.env.PMD_HOME ? `${process.env.PMD_HOME}/bin/pmd` : null,
  ].filter(Boolean) as string[];

  for (const candidate of candidates) {
    try {
      const result = await runCommand(candidate, ['--version'], { timeout: 5000 });
      if (result.exitCode === 0 && result.stdout.includes('PMD')) {
        log.debug(`Found PMD at: ${candidate}`);
        return candidate;
      }
    } catch {
      // Continue to next candidate
    }
  }

  return null;
}

/**
 * Run a command and capture output
 */
function runCommand(
  command: string,
  args: string[],
  options: { timeout?: number; cwd?: string } = {}
): Promise<{ stdout: string; stderr: string; exitCode: number }> {
  return new Promise((resolve, reject) => {
    const proc = spawn(command, args, {
      cwd: options.cwd,
      shell: true,
      stdio: ['pipe', 'pipe', 'pipe'],
    });

    let stdout = '';
    let stderr = '';

    proc.stdout.on('data', (data) => {
      stdout += data.toString();
    });

    proc.stderr.on('data', (data) => {
      stderr += data.toString();
    });

    const timeout = options.timeout || 60000;
    const timer = setTimeout(() => {
      proc.kill('SIGTERM');
      reject(new Error(`Command timed out after ${timeout}ms`));
    }, timeout);

    proc.on('close', (code) => {
      clearTimeout(timer);
      resolve({ stdout, stderr, exitCode: code ?? 1 });
    });

    proc.on('error', (err) => {
      clearTimeout(timer);
      reject(err);
    });
  });
}

/**
 * PMD Executor class
 */
export class PmdExecutor {
  private pmdPath: string | null = null;
  private options: PmdExecutorOptions;

  constructor(options: PmdExecutorOptions = {}) {
    this.options = options;
    this.pmdPath = options.pmdPath || null;
  }

  /**
   * Initialize and detect PMD
   */
  async initialize(): Promise<boolean> {
    if (this.pmdPath) {
      return true;
    }

    this.pmdPath = await detectPmdPath();
    if (!this.pmdPath) {
      log.error('PMD not found. Please install PMD (e.g., "brew install pmd")');
      return false;
    }

    log.info(`PMD detected at: ${this.pmdPath}`);
    return true;
  }

  /**
   * Get PMD version
   */
  async getVersion(): Promise<string | null> {
    if (!this.pmdPath) {
      await this.initialize();
    }
    if (!this.pmdPath) return null;

    try {
      const result = await runCommand(this.pmdPath, ['--version'], {
        timeout: 5000,
      });
      // Extract version from output like "PMD 7.19.0 (...)"
      const match = result.stdout.match(/PMD\s+(\d+\.\d+\.\d+)/);
      return match ? match[1] : null;
    } catch {
      return null;
    }
  }

  /**
   * Run PMD check analysis
   */
  async check(options: PmdCheckOptions): Promise<PmdResult> {
    if (!this.pmdPath) {
      await this.initialize();
    }
    if (!this.pmdPath) {
      return {
        success: false,
        violations: [],
        filesAnalyzed: 0,
        processingErrors: ['PMD not found. Please install PMD.'],
        suppressedViolations: 0,
      };
    }

    // Validate path exists
    if (!existsSync(options.path)) {
      return {
        success: false,
        violations: [],
        filesAnalyzed: 0,
        processingErrors: [`Path not found: ${options.path}`],
        suppressedViolations: 0,
      };
    }

    // Build command arguments
    const args = ['check', '-d', options.path, '-f', 'json'];

    // Add rulesets
    if (options.rulesets && options.rulesets.length > 0) {
      args.push('-R', options.rulesets.join(','));
    } else {
      // Default to quickstart ruleset for Java, or category-based for others
      args.push('-R', 'rulesets/java/quickstart.xml');
    }

    // Add language version if specified
    if (options.languageVersion) {
      args.push('--use-version', options.languageVersion);
    }

    // Add minimum priority
    if (options.minimumPriority) {
      args.push('--minimum-priority', options.minimumPriority.toString());
    }

    // Add excludes
    if (options.excludes) {
      for (const exclude of options.excludes) {
        args.push('--exclude', exclude);
      }
    }

    // Add threads
    if (options.threads) {
      args.push('-t', options.threads.toString());
    }

    log.debug(`Running: ${this.pmdPath} ${args.join(' ')}`);

    try {
      const result = await runCommand(this.pmdPath, args, {
        timeout: this.options.timeout || 60000,
        cwd: this.options.cwd,
      });

      // PMD returns exit code 4 when violations are found
      if (result.exitCode !== 0 && result.exitCode !== 4) {
        return {
          success: false,
          violations: [],
          filesAnalyzed: 0,
          processingErrors: [result.stderr || `PMD exited with code ${result.exitCode}`],
          suppressedViolations: 0,
        };
      }

      return this.parseJsonOutput(result.stdout);
    } catch (err) {
      return {
        success: false,
        violations: [],
        filesAnalyzed: 0,
        processingErrors: [(err as Error).message],
        suppressedViolations: 0,
      };
    }
  }

  /**
   * Run CPD (Copy-Paste Detector)
   */
  async cpd(options: CpdCheckOptions): Promise<CpdResult> {
    if (!this.pmdPath) {
      await this.initialize();
    }
    if (!this.pmdPath) {
      return {
        success: false,
        duplications: [],
        filesAnalyzed: 0,
        processingErrors: ['PMD not found. Please install PMD.'],
      };
    }

    // Validate path exists
    if (!existsSync(options.path)) {
      return {
        success: false,
        duplications: [],
        filesAnalyzed: 0,
        processingErrors: [`Path not found: ${options.path}`],
      };
    }

    // Build command arguments
    const args = [
      'cpd',
      '-d', options.path,
      '--language', options.language,
      '--minimum-tokens', (options.minimumTokens || 50).toString(),
      '-f', 'xml', // XML is easier to parse for CPD
    ];

    // Add options
    if (options.skipDuplicateFiles) {
      args.push('--skip-duplicate-files');
    }
    if (options.ignoreLiterals) {
      args.push('--ignore-literals');
    }
    if (options.ignoreIdentifiers) {
      args.push('--ignore-identifiers');
    }

    // Add excludes
    if (options.excludes) {
      for (const exclude of options.excludes) {
        args.push('--exclude', exclude);
      }
    }

    log.debug(`Running: ${this.pmdPath} ${args.join(' ')}`);

    try {
      const result = await runCommand(this.pmdPath, args, {
        timeout: this.options.timeout || 60000,
        cwd: this.options.cwd,
      });

      // CPD returns exit code 4 when duplications are found
      if (result.exitCode !== 0 && result.exitCode !== 4) {
        return {
          success: false,
          duplications: [],
          filesAnalyzed: 0,
          processingErrors: [result.stderr || `CPD exited with code ${result.exitCode}`],
        };
      }

      return this.parseCpdXmlOutput(result.stdout);
    } catch (err) {
      return {
        success: false,
        duplications: [],
        filesAnalyzed: 0,
        processingErrors: [(err as Error).message],
      };
    }
  }

  /**
   * Parse PMD JSON output
   */
  private parseJsonOutput(output: string): PmdResult {
    try {
      const data = JSON.parse(output);
      const violations: PmdViolation[] = [];
      let filesAnalyzed = 0;

      // PMD JSON format has files array with violations
      if (data.files) {
        filesAnalyzed = data.files.length;
        for (const file of data.files) {
          for (const v of file.violations || []) {
            violations.push({
              file: file.filename,
              line: v.beginline,
              column: v.begincolumn,
              endLine: v.endline,
              endColumn: v.endcolumn,
              rule: v.rule,
              ruleset: v.ruleset,
              priority: v.priority,
              message: v.description,
              externalInfoUrl: v.externalInfoUrl,
            });
          }
        }
      }

      return {
        success: true,
        violations,
        filesAnalyzed,
        processingErrors: data.processingErrors || [],
        suppressedViolations: data.suppressedViolations?.length || 0,
      };
    } catch {
      // If JSON parsing fails, try to extract info from text
      return {
        success: true,
        violations: [],
        filesAnalyzed: 0,
        processingErrors: ['Failed to parse PMD output'],
        suppressedViolations: 0,
      };
    }
  }

  /**
   * Parse CPD XML output
   */
  private parseCpdXmlOutput(output: string): CpdResult {
    const duplications: CpdDuplication[] = [];
    let filesAnalyzed = 0;

    try {
      // Simple XML parsing for CPD output
      const duplicationMatches = output.matchAll(
        /<duplication lines="(\d+)" tokens="(\d+)">([\s\S]*?)<\/duplication>/g
      );

      for (const match of duplicationMatches) {
        const lines = parseInt(match[1], 10);
        const tokens = parseInt(match[2], 10);
        const content = match[3];

        const files: CpdDuplication['files'] = [];
        const fileMatches = content.matchAll(
          /<file\s+line="(\d+)"\s+(?:endline="(\d+)"\s+)?(?:column="\d+"\s+)?(?:endcolumn="\d+"\s+)?path="([^"]+)"/g
        );

        for (const fileMatch of fileMatches) {
          files.push({
            path: fileMatch[3],
            startLine: parseInt(fileMatch[1], 10),
            endLine: parseInt(fileMatch[2] || fileMatch[1], 10) + lines - 1,
          });
        }

        // Extract code fragment if present
        const codeMatch = content.match(/<codefragment>([\s\S]*?)<\/codefragment>/);
        const codefragment = codeMatch ? codeMatch[1].trim() : undefined;

        duplications.push({ lines, tokens, files, codefragment });
      }

      // Count unique files
      const uniqueFiles = new Set<string>();
      for (const dup of duplications) {
        for (const file of dup.files) {
          uniqueFiles.add(file.path);
        }
      }
      filesAnalyzed = uniqueFiles.size;

      return {
        success: true,
        duplications,
        filesAnalyzed,
        processingErrors: [],
      };
    } catch {
      return {
        success: false,
        duplications: [],
        filesAnalyzed: 0,
        processingErrors: ['Failed to parse CPD output'],
      };
    }
  }

  /**
   * List supported languages
   */
  async listLanguages(): Promise<string[]> {
    // PMD supported languages (from documentation)
    return [
      'apex',
      'ecmascript',
      'html',
      'java',
      'jsp',
      'kotlin',
      'modelica',
      'plsql',
      'pom',
      'scala',
      'swift',
      'velocity',
      'visualforce',
      'wsdl',
      'xml',
      'xsl',
    ];
  }

  /**
   * List CPD supported languages
   */
  async listCpdLanguages(): Promise<string[]> {
    return [
      'apex',
      'coco',
      'cpp',
      'cs',
      'css',
      'dart',
      'ecmascript',
      'fortran',
      'gherkin',
      'go',
      'groovy',
      'html',
      'java',
      'jsp',
      'julia',
      'kotlin',
      'lua',
      'matlab',
      'modelica',
      'objectivec',
      'perl',
      'php',
      'plsql',
      'python',
      'ruby',
      'rust',
      'scala',
      'swift',
      'tsql',
      'typescript',
      'velocity',
      'visualforce',
      'wsdl',
      'xml',
      'xsl',
    ];
  }
}

// Default executor instance
export const pmdExecutor = new PmdExecutor();
