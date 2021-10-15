/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin.ast;


import net.sourceforge.pmd.lang.ast.test.BaseTreeDumpTest;
import net.sourceforge.pmd.lang.ast.test.NodePrintersKt;
import org.checkerframework.checker.nullness.qual.NonNull;

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
