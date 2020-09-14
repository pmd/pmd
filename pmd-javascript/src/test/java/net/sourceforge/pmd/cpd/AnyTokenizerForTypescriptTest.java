/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.Properties;

import org.junit.Test;

import net.sourceforge.pmd.cpd.test.CpdTextComparisonTest;

/**
 *
 */
public class AnyTokenizerForTypescriptTest extends CpdTextComparisonTest {

    public AnyTokenizerForTypescriptTest() {
        super(".ts");
    }

    @Override
    protected String getResourcePrefix() {
        return "testdata/ts";
    }

    @Override
    public Tokenizer newTokenizer(Properties properties) {
        return new AnyTokenizer();
    }

    @Test
    public void testFile1() {
        doTest("SampleTypeScript");
    }

}
