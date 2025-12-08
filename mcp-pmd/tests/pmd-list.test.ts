/**
 * Tests for PMD List Tools
 */

import { describe, it, expect } from 'vitest';
import { executeListLanguages, executeListRulesets } from '../src/tools/pmd-list.js';

describe('executeListLanguages', () => {
  it('should return formatted language list', async () => {
    const result = await executeListLanguages();
    
    expect(typeof result).toBe('string');
    expect(result).toContain('PMD Supported Languages');
    expect(result).toContain('Static Analysis');
    expect(result).toContain('Copy-Paste Detection');
    expect(result).toContain('java');
    expect(result).toContain('python');
  });

  it('should include both PMD and CPD languages', async () => {
    const result = await executeListLanguages();
    
    // PMD-specific languages
    expect(result).toContain('apex');
    expect(result).toContain('ecmascript');
    
    // CPD-specific languages
    expect(result).toContain('typescript');
    expect(result).toContain('go');
    expect(result).toContain('ruby');
  });
});

describe('executeListRulesets', () => {
  it('should return all rulesets when no language specified', async () => {
    const result = await executeListRulesets();
    
    expect(typeof result).toBe('string');
    expect(result).toContain('PMD Rulesets');
    expect(result).toContain('java');
    expect(result).toContain('apex');
    expect(result).toContain('ecmascript');
  });

  it('should filter by Java language', async () => {
    const result = await executeListRulesets('java');
    
    expect(result).toContain('java');
    expect(result).toContain('bestpractices');
    expect(result).toContain('errorprone');
    expect(result).toContain('quickstart');
  });

  it('should filter by Apex language', async () => {
    const result = await executeListRulesets('apex');
    
    expect(result).toContain('apex');
    expect(result).toContain('security');
  });

  it('should filter by JavaScript (ecmascript)', async () => {
    const result = await executeListRulesets('ecmascript');
    
    expect(result).toContain('ecmascript');
    expect(result).toContain('bestpractices');
  });

  it('should handle unknown language gracefully', async () => {
    const result = await executeListRulesets('unknownlang');
    
    expect(result).toContain('No predefined rulesets found');
    expect(result).toContain('category-based rulesets');
  });

  it('should include usage instructions', async () => {
    const result = await executeListRulesets();
    
    expect(result).toContain('Usage');
    expect(result).toContain('pmd_check');
  });
});
