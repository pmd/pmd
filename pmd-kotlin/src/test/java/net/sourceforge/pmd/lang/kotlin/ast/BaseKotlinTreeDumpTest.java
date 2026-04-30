/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.ast;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.test.ast.BaseTreeDumpTest;
import net.sourceforge.pmd.lang.test.ast.NodePrintersKt;

/**
 * Base class for tree dump tests for Kotlin. It uses the simple node printer, which prints the node type and its image.
 */
public class BaseKotlinTreeDumpTest extends BaseTreeDumpTest {

    public BaseKotlinTreeDumpTest() {
        super(NodePrintersKt.getSimpleNodePrinter(), ".kt");
    }

    @NonNull
    @Override
    public KotlinParsingHelper getParser() {
        return KotlinParsingHelper.DEFAULT.withResourceContext(getClass(), "testdata");
    }
}
