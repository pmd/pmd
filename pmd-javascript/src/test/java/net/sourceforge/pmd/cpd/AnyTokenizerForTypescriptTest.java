/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.Properties;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.cpd.test.CpdTextComparisonTest;

/**
 *
 */
class AnyTokenizerForTypescriptTest extends CpdTextComparisonTest {

    AnyTokenizerForTypescriptTest() {
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
    void testFile1() {
        doTest("SampleTypeScript");
    }

}
