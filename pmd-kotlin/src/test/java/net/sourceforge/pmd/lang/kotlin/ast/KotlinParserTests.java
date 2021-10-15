/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.ast;

import org.junit.Test;

/**
 *
 */
public class KotlinParserTests extends BaseKotlinTreeDumpTest {

    @Test
    public void testSimpleKotlin() {
        doTest("Simple");
    }

//    @Test
//    public void testBtree() {
//        doTest("BTree");
//    }

}
