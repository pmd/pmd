/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift.ast;

import org.junit.jupiter.api.Test;

class SwiftParserTests extends BaseSwiftTreeDumpTest {

    @Test
    void testSimpleSwift() {
        doTest("Simple");
    }

    @Test
    void testBtree() {
        doTest("BTree");
    }

    @Test
    void swift59() {
        doTest("Swift5.9");
    }

    @Test
    void macroExpansions() {
        doTest("MacroExpansionExpressions");
    }
}
