/**
 * Tests for PMD CPD Tool
 */

import { describe, it, expect } from 'vitest';
import { join } from 'path';
import { executePmdCpd, pmdCpdSchema } from '../src/tools/pmd-cpd.js';

describe('pmdCpdSchema', () => {
  it('should validate valid input', () => {
    const input = {
      path: '/some/path',
      language: 'python',
      minimum_tokens: 75,
    };
    
    const result = pmdCpdSchema.safeParse(input);
    expect(result.success).toBe(true);
  });

  it('should require path and language', () => {
    const missingLanguage = { path: '/path' };
    const missingPath = { language: 'python' };
    const valid = { path: '/path', language: 'python' };
    
    expect(pmdCpdSchema.safeParse(missingLanguage).success).toBe(false);
    expect(pmdCpdSchema.safeParse(missingPath).success).toBe(false);
    expect(pmdCpdSchema.safeParse(valid).success).toBe(true);
  });

  it('should accept optional fields', () => {
    const fullInput = {
      path: '/path',
      language: 'java',
      minimum_tokens: 100,
      ignore_literals: true,
      ignore_identifiers: true,
      excludes: ['**/vendor/**'],
    };
    
    expect(pmdCpdSchema.safeParse(fullInput).success).toBe(true);
  });

  it('should default minimum_tokens to 50', () => {
    const input = { path: '/path', language: 'python' };
    const result = pmdCpdSchema.parse(input);
    expect(result.minimum_tokens).toBe(50);
  });
});

describe('executePmdCpd', () => {
  it('should return error for non-existent path', async () => {
    const result = await executePmdCpd({
      path: '/non/existent/path/that/does/not/exist',
      language: 'python',
      minimum_tokens: 50,
    });
    
    expect(result).toContain('Path not found');
  });

  it('should handle various languages', async () => {
    // Test with src directory - smaller scope for faster test
    const result = await executePmdCpd({
      path: join(process.cwd(), 'src'),
      language: 'typescript',
      minimum_tokens: 100,
    });
    
    expect(typeof result).toBe('string');
    expect(result).toContain('CPD');
  }, 60000); // Increase timeout for CPD
});
