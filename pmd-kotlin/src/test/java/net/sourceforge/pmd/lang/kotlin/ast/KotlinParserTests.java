/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.ast;

import org.junit.jupiter.api.Test;

/**
 * Minimal test that parses a simple Kotlin snippet and see if there are no parsing issues.
 */
class KotlinParserTests extends BaseKotlinTreeDumpTest {

    @Test
    void testSimpleKotlin() {
        doTest("Simple");
    }

}
