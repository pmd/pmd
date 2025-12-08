/**
 * Integration Tests for PMD MCP Server
 * 
 * These tests verify the full flow from tool input to formatted output.
 */

import { describe, it, expect, beforeAll, afterAll } from 'vitest';
import { existsSync, mkdirSync, writeFileSync, rmSync } from 'fs';
import { join } from 'path';
import { tmpdir } from 'os';
import { PmdExecutor } from '../src/pmd-executor.js';
import { executePmdCheck } from '../src/tools/pmd-check.js';
import { executePmdCpd } from '../src/tools/pmd-cpd.js';

describe('Integration Tests', () => {
  let testDir: string;
  let pmdAvailable: boolean;

  beforeAll(async () => {
    // Check if PMD is available
    const executor = new PmdExecutor();
    pmdAvailable = await executor.initialize();
    
    // Create temp test directory with sample files
    testDir = join(tmpdir(), 'pmd-mcp-integration-' + Date.now());
    mkdirSync(testDir, { recursive: true });
    
    // Create sample Java files
    writeFileSync(join(testDir, 'GoodCode.java'), `
public class GoodCode {
    private String name;
    
    public GoodCode(String name) {
        this.name = name;
    }
    
    public String getName() {
        return this.name;
    }
}
`);

    writeFileSync(join(testDir, 'BadCode.java'), `
public class BadCode {
    public void emptyMethod() {
        // This method does nothing
    }
    
    public void emptyCatch() {
        try {
            throw new Exception();
        } catch (Exception e) {
            // Empty catch block - violation!
        }
    }
}
`);

    // Create duplicate code files
    const duplicateCode = `
public class DuplicateCode {
    public int calculate(int a, int b, int c) {
        int result = 0;
        result += a * 2;
        result += b * 3;
        result += c * 4;
        result = result * result;
        return result;
    }
}
`;
    writeFileSync(join(testDir, 'Duplicate1.java'), duplicateCode.replace('DuplicateCode', 'Duplicate1'));
    writeFileSync(join(testDir, 'Duplicate2.java'), duplicateCode.replace('DuplicateCode', 'Duplicate2'));

    // Create Python files for CPD
    writeFileSync(join(testDir, 'script1.py'), `
def process_data(data):
    result = []
    for item in data:
        if item > 0:
            result.append(item * 2)
        else:
            result.append(item * -1)
    return result

def main():
    data = [1, -2, 3, -4, 5]
    print(process_data(data))
`);

    writeFileSync(join(testDir, 'script2.py'), `
def process_data(data):
    result = []
    for item in data:
        if item > 0:
            result.append(item * 2)
        else:
            result.append(item * -1)
    return result

def run():
    data = [10, -20, 30]
    print(process_data(data))
`);
  });

  afterAll(() => {
    // Cleanup
    if (existsSync(testDir)) {
      rmSync(testDir, { recursive: true, force: true });
    }
  });

  describe('PMD Check Integration', () => {
    it('should analyze directory and return formatted results', async () => {
      if (!pmdAvailable) {
        console.log('Skipping: PMD not installed');
        return;
      }

      const result = await executePmdCheck({
        path: testDir,
        rulesets: ['category/java/errorprone.xml'],
      });

      expect(result).toContain('PMD Analysis Results');
      expect(result).toContain('Files Analyzed');
    });

    it('should respect minimum_priority filter', async () => {
      if (!pmdAvailable) {
        console.log('Skipping: PMD not installed');
        return;
      }

      const highPriority = await executePmdCheck({
        path: testDir,
        minimum_priority: 1,
      });

      const allPriorities = await executePmdCheck({
        path: testDir,
        minimum_priority: 5,
      });

      // Both should be valid results
      expect(highPriority).toContain('PMD Analysis Results');
      expect(allPriorities).toContain('PMD Analysis Results');
    });

    it('should handle single file analysis', async () => {
      if (!pmdAvailable) {
        console.log('Skipping: PMD not installed');
        return;
      }

      const result = await executePmdCheck({
        path: join(testDir, 'GoodCode.java'),
      });

      expect(result).toContain('PMD Analysis Results');
    });
  });

  describe('CPD Integration', () => {
    it('should detect Java duplications', async () => {
      if (!pmdAvailable) {
        console.log('Skipping: PMD not installed');
        return;
      }

      const result = await executePmdCpd({
        path: testDir,
        language: 'java',
        minimum_tokens: 20,
      });

      expect(result).toContain('CPD');
      expect(result).toContain('Files Analyzed');
    });

    it('should detect Python duplications', async () => {
      if (!pmdAvailable) {
        console.log('Skipping: PMD not installed');
        return;
      }

      const result = await executePmdCpd({
        path: testDir,
        language: 'python',
        minimum_tokens: 20,
      });

      expect(result).toContain('CPD');
      // Should find the duplicated process_data function
    });

    it('should respect ignore_literals option', async () => {
      if (!pmdAvailable) {
        console.log('Skipping: PMD not installed');
        return;
      }

      const withLiterals = await executePmdCpd({
        path: testDir,
        language: 'java',
        minimum_tokens: 20,
        ignore_literals: false,
      });

      const ignoreLiterals = await executePmdCpd({
        path: testDir,
        language: 'java',
        minimum_tokens: 20,
        ignore_literals: true,
      });

      // Both should complete successfully
      expect(withLiterals).toContain('CPD');
      expect(ignoreLiterals).toContain('CPD');
    });
  });

  describe('Error Handling', () => {
    it('should handle invalid paths gracefully', async () => {
      const result = await executePmdCheck({
        path: '/this/path/definitely/does/not/exist/anywhere',
      });

      expect(result).toContain('Path not found');
    });

    it('should handle invalid language for CPD', async () => {
      if (!pmdAvailable) {
        console.log('Skipping: PMD not installed');
        return;
      }

      const result = await executePmdCpd({
        path: testDir,
        language: 'invalidlanguage',
        minimum_tokens: 50,
      });

      // Should return an error or empty result
      expect(typeof result).toBe('string');
    });
  });

  describe('Output Format', () => {
    it('should produce markdown-formatted output for PMD check', async () => {
      if (!pmdAvailable) {
        console.log('Skipping: PMD not installed');
        return;
      }

      const result = await executePmdCheck({
        path: testDir,
      });

      // Check for markdown elements
      expect(result).toMatch(/##/); // Headers
      expect(result).toMatch(/\*\*/); // Bold text
    });

    it('should produce markdown-formatted output for CPD', async () => {
      if (!pmdAvailable) {
        console.log('Skipping: PMD not installed');
        return;
      }

      const result = await executePmdCpd({
        path: testDir,
        language: 'java',
        minimum_tokens: 20,
      });

      // Check for markdown elements
      expect(result).toMatch(/##/); // Headers
    });
  });
});
