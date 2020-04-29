/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift.ast;

import org.junit.Test;

/**
 *
 */
public class SwiftParserTests extends BaseSwiftTreeDumpTest {

    @Test
    public void testSimpleSwift() {
        doTest("Simple");
    }

    @Test
    public void testBtree() {
        doTest("BTree");
    }

}
