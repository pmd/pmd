/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.ast;

import org.junit.jupiter.api.Test;

/**
 *
 */
class KotlinParserTests extends BaseKotlinTreeDumpTest {

    @Test
    void testSimpleKotlin() {
        doTest("Simple");
    }

}
