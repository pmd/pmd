/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift.ast;


import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.ast.test.BaseTreeDumpTest;
import net.sourceforge.pmd.lang.ast.test.NodePrintersKt;

/**
 *
 */
public class BaseSwiftTreeDumpTest extends BaseTreeDumpTest {

    public BaseSwiftTreeDumpTest() {
        super(NodePrintersKt.getSimpleNodePrinter(), ".swift");
    }

    @NonNull
    @Override
    public SwiftParsingHelper getParser() {
        return SwiftParsingHelper.DEFAULT.withResourceContext(getClass(), "testdata");
    }
}
