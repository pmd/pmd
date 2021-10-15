/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.ast;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.ast.test.BaseTreeDumpTest;
import net.sourceforge.pmd.lang.ast.test.NodePrintersKt;

/**
 *
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
