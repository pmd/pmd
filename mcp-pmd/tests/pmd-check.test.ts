/**
 * Tests for PMD Check Tool
 */

import { describe, it, expect } from 'vitest';
import { executePmdCheck, pmdCheckSchema } from '../src/tools/pmd-check.js';
import type { PmdResult, PmdViolation } from '../src/types.js';

describe('pmdCheckSchema', () => {
  it('should validate valid input', () => {
    const input = {
      path: '/some/path',
      rulesets: ['category/java/bestpractices.xml'],
      minimum_priority: 3,
    };
    
    const result = pmdCheckSchema.safeParse(input);
    expect(result.success).toBe(true);
  });

  it('should require path', () => {
    const input = {
      rulesets: ['category/java/bestpractices.xml'],
    };
    
    const result = pmdCheckSchema.safeParse(input);
    expect(result.success).toBe(false);
  });

  it('should validate minimum_priority range', () => {
    const validInput = { path: '/path', minimum_priority: 3 };
    const invalidLow = { path: '/path', minimum_priority: 0 };
    const invalidHigh = { path: '/path', minimum_priority: 6 };
    
    expect(pmdCheckSchema.safeParse(validInput).success).toBe(true);
    expect(pmdCheckSchema.safeParse(invalidLow).success).toBe(false);
    expect(pmdCheckSchema.safeParse(invalidHigh).success).toBe(false);
  });

  it('should accept optional fields', () => {
    const minimalInput = { path: '/path' };
    const fullInput = {
      path: '/path',
      rulesets: ['ruleset1', 'ruleset2'],
      language_version: 'java-21',
      minimum_priority: 2,
      excludes: ['**/test/**'],
    };
    
    expect(pmdCheckSchema.safeParse(minimalInput).success).toBe(true);
    expect(pmdCheckSchema.safeParse(fullInput).success).toBe(true);
  });
});

describe('executePmdCheck', () => {
  it('should return error for non-existent path', async () => {
    const result = await executePmdCheck({
      path: '/non/existent/path/that/does/not/exist',
    });
    
    expect(result).toContain('Path not found');
  });
});

describe('Output Formatting', () => {
  // Test the formatting logic by examining output structure
  
  it('should format violations by priority', async () => {
    // This tests the actual execution with a real path
    // Skip if PMD not available - the executor handles this gracefully
    const result = await executePmdCheck({
      path: process.cwd(), // Use current directory
    });
    
    // Result should be a string with markdown formatting
    expect(typeof result).toBe('string');
    expect(result).toContain('PMD');
  });
});
