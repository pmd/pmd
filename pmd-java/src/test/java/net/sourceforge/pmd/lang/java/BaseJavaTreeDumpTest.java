/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java;

import net.sourceforge.pmd.lang.ast.test.BaseTreeDumpTest;

/**
 * Special tweak to remove deprecated attributes of AccessNode
 */
public abstract class BaseJavaTreeDumpTest extends BaseTreeDumpTest {

    protected BaseJavaTreeDumpTest() {
        super(new JavaAttributesPrinter(), ".java");
    }
}
