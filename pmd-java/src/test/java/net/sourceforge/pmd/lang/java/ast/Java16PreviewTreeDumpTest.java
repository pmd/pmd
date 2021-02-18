/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.junit.Test;

import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.test.BaseParsingHelper;
import net.sourceforge.pmd.lang.ast.test.BaseTreeDumpTest;
import net.sourceforge.pmd.lang.ast.test.RelevantAttributePrinter;
import net.sourceforge.pmd.lang.java.JavaParsingHelper;

public class Java16PreviewTreeDumpTest extends BaseTreeDumpTest {
    private final JavaParsingHelper java16p =
            JavaParsingHelper.WITH_PROCESSING.withDefaultVersion("16-preview")
                    .withResourceContext(Java16PreviewTreeDumpTest.class, "jdkversiontests/java16p/");
    private final JavaParsingHelper java16 = java16p.withDefaultVersion("16");

    public Java16PreviewTreeDumpTest() {
        super(new RelevantAttributePrinter(), ".java");
    }

    @Override
    public BaseParsingHelper<?, ?> getParser() {
        return java16p;
    }

    @Test(expected = ParseException.class)
    public void sealedClassBeforeJava16Preview() {
        java16.parseResource("geometry/Shape.java");
    }

    @Test
    public void sealedClass() {
        doTest("geometry/Shape");
    }

    @Test
    public void nonSealedClass() {
        doTest("geometry/Square");
    }

    @Test(expected = ParseException.class)
    public void sealedInterfaceBeforeJava15Preview() {
        java16.parseResource("expression/Expr.java");
    }

    @Test
    public void sealedInterface() {
        doTest("expression/Expr");
    }
}
