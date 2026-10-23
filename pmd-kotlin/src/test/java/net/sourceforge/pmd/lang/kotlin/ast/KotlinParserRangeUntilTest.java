/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.ast;

import org.junit.jupiter.api.Test;

/**
 * Tree dump tests for the open-ended range operator.
 */
class KotlinParserRangeUntilTest extends BaseKotlinTreeDumpTest {

    @Test
    void testRangeUntil() {
        doTest("RangeUntil");
    }
}
