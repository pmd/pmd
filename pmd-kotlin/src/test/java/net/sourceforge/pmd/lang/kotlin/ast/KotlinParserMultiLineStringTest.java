/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.ast;

import org.junit.jupiter.api.Test;

/**
 * Tree dump tests for Kotlin multi-line string parsing.
 */
class KotlinParserMultiLineStringTest extends BaseKotlinTreeDumpTest {

    @Test
    void testMultiLineStringRefs() {
        doTest("MultiLineStringRefs");
    }

    @Test
    void testMultiLineStringExpressions() {
        doTest("MultiLineStringExpressions");
    }

    @Test
    void testSingleDollarRefWithoutPrefix() {
        doTest("SingleDollarRefNoPrefix");
    }

    @Test
    void testMultiDollarRefSplitting() {
        doTest("MultiLineStringRefSplitting");
    }

    @Test
    void testMultiDollarExpressionSplitting() {
        doTest("MultiLineStringExprSplitting");
    }

    @Test
    void testSingleDollarExpressionWithoutPrefix() {
        doTest("SingleDollarExprNoPrefix");
    }
}
