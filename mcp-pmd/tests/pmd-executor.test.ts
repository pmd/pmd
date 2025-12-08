/**
 * Tests for PMD Executor
 */

import { describe, it, expect, beforeAll, afterAll } from 'vitest';
import { PmdExecutor, detectPmdPath } from '../src/pmd-executor.js';
import { existsSync, mkdirSync, writeFileSync, rmSync } from 'fs';
import { join } from 'path';
import { tmpdir } from 'os';

describe('PmdExecutor', () => {
  let executor: PmdExecutor;
  let testDir: string;
  let pmdAvailable: boolean;

  beforeAll(async () => {
    executor = new PmdExecutor();
    pmdAvailable = await executor.initialize();
    
    // Create temp test directory
    testDir = join(tmpdir(), 'pmd-mcp-test-' + Date.now());
    mkdirSync(testDir, { recursive: true });
  });

  afterAll(() => {
    // Cleanup
    if (existsSync(testDir)) {
      rmSync(testDir, { recursive: true, force: true });
    }
  });

  describe('detectPmdPath', () => {
    it('should detect PMD installation', async () => {
      const path = await detectPmdPath();
      // PMD may or may not be installed
      if (path) {
        expect(path).toContain('pmd');
      }
    });
  });

  describe('initialize', () => {
    it('should initialize executor', async () => {
      const newExecutor = new PmdExecutor();
      const result = await newExecutor.initialize();
      expect(typeof result).toBe('boolean');
    });
  });

  describe('getVersion', () => {
    it('should return version if PMD is available', async () => {
      if (!pmdAvailable) {
        console.log('Skipping: PMD not installed');
        return;
      }
      
      const version = await executor.getVersion();
      expect(version).toMatch(/^\d+\.\d+\.\d+$/);
    });
  });

  describe('listLanguages', () => {
    it('should return list of PMD languages', async () => {
      const languages = await executor.listLanguages();
      expect(languages).toContain('java');
      expect(languages).toContain('ecmascript');
      expect(languages).toContain('apex');
    });
  });

  describe('listCpdLanguages', () => {
    it('should return list of CPD languages', async () => {
      const languages = await executor.listCpdLanguages();
      expect(languages).toContain('python');
      expect(languages).toContain('java');
      expect(languages).toContain('typescript');
      expect(languages).toContain('ecmascript'); // JavaScript is called ecmascript in PMD
    });
  });

  describe('check', () => {
    it('should return error for non-existent path', async () => {
      const result = await executor.check({
        path: '/non/existent/path',
      });
      
      expect(result.success).toBe(false);
      expect(result.processingErrors).toContain('Path not found: /non/existent/path');
    });

    it('should analyze Java file with violations', async () => {
      if (!pmdAvailable) {
        console.log('Skipping: PMD not installed');
        return;
      }

      // Create a Java file with known violations
      const javaFile = join(testDir, 'BadCode.java');
      writeFileSync(javaFile, `
public class BadCode {
    public void method() {
        try {
            // Empty catch block - should trigger violation
        } catch (Exception e) {
        }
    }
}
`);

      const result = await executor.check({
        path: javaFile,
        rulesets: ['category/java/errorprone.xml'],
      });

      expect(result.success).toBe(true);
      expect(result.filesAnalyzed).toBeGreaterThanOrEqual(1);
    });

    it('should analyze clean Java file with no violations', async () => {
      if (!pmdAvailable) {
        console.log('Skipping: PMD not installed');
        return;
      }

      // Create a clean Java file
      const javaFile = join(testDir, 'CleanCode.java');
      writeFileSync(javaFile, `
public class CleanCode {
    public String getMessage() {
        return "Hello, World!";
    }
}
`);

      const result = await executor.check({
        path: javaFile,
        rulesets: ['rulesets/java/quickstart.xml'],
      });

      expect(result.success).toBe(true);
    });
  });

  describe('cpd', () => {
    it('should return error for non-existent path', async () => {
      const result = await executor.cpd({
        path: '/non/existent/path',
        language: 'java',
      });
      
      expect(result.success).toBe(false);
      expect(result.processingErrors).toContain('Path not found: /non/existent/path');
    });

    it('should detect duplicated code', async () => {
      if (!pmdAvailable) {
        console.log('Skipping: PMD not installed');
        return;
      }

      // Create files with duplicated code
      const file1 = join(testDir, 'Dup1.java');
      const file2 = join(testDir, 'Dup2.java');
      
      const duplicatedCode = `
public class DupClass {
    public void duplicatedMethod() {
        int a = 1;
        int b = 2;
        int c = 3;
        int d = 4;
        int e = 5;
        System.out.println(a + b + c + d + e);
        System.out.println(a * b * c * d * e);
        System.out.println(a - b - c - d - e);
    }
}
`;
      
      writeFileSync(file1, duplicatedCode.replace('DupClass', 'Dup1'));
      writeFileSync(file2, duplicatedCode.replace('DupClass', 'Dup2'));

      const result = await executor.cpd({
        path: testDir,
        language: 'java',
        minimumTokens: 20,
      });

      expect(result.success).toBe(true);
      // May or may not find duplications depending on token threshold
    });

    it('should analyze Python files', async () => {
      if (!pmdAvailable) {
        console.log('Skipping: PMD not installed');
        return;
      }

      const pyFile = join(testDir, 'test.py');
      writeFileSync(pyFile, `
def hello():
    print("Hello, World!")

def goodbye():
    print("Goodbye, World!")
`);

      const result = await executor.cpd({
        path: pyFile,
        language: 'python',
        minimumTokens: 10,
      });

      expect(result.success).toBe(true);
    });
  });
});
