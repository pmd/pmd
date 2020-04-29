/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift.ast;

import org.jetbrains.annotations.NotNull;

import net.sourceforge.pmd.lang.ast.test.BaseTreeDumpTest;
import net.sourceforge.pmd.lang.ast.test.NodePrintersKt;

/**
 *
 */
public class BaseSwiftTreeDumpTest extends BaseTreeDumpTest {

    public BaseSwiftTreeDumpTest() {
        super(NodePrintersKt.getSimpleNodePrinter(), ".swift");
    }

    @NotNull
    @Override
    public SwiftParsingHelper getParser() {
        return SwiftParsingHelper.DEFAULT.withResourceContext(getClass(), "testdata");
    }
}
