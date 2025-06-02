/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java;

import net.sourceforge.pmd.lang.test.ast.BaseTreeDumpTest;

/**
 * Special tweak of BaseTreeDumpTest to remove deprecated attributes
 * of nodes.
 */
public abstract class BaseJavaTreeDumpTest extends BaseTreeDumpTest {

    protected BaseJavaTreeDumpTest() {
        super(new JavaAttributesPrinter(), ".java");
    }
}
