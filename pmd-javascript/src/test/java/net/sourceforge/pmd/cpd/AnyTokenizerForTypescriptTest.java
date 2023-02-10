/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

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

    @Test
    void testFile1() {
        doTest("SampleTypeScript");
    }

}
