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

public class Java17TreeDumpTest extends BaseTreeDumpTest {
    private final JavaParsingHelper java17 =
            JavaParsingHelper.WITH_PROCESSING.withDefaultVersion("17")
                    .withResourceContext(Java17TreeDumpTest.class, "jdkversiontests/java17/");
    private final JavaParsingHelper java17p = java17.withDefaultVersion("17-preview");
    private final JavaParsingHelper java16 = java17.withDefaultVersion("16");

    public Java17TreeDumpTest() {
        super(new RelevantAttributePrinter(), ".java");
    }

    @Override
    public BaseParsingHelper<?, ?> getParser() {
        return java17;
    }

    @Test(expected = ParseException.class)
    public void sealedClassBeforeJava17() {
        java16.parseResource("geometry/Shape.java");
    }

    @Test
    public void sealedClass() {
        doTest("geometry/Shape");
        java17p.parseResource("geometry/Shape.java"); // make sure we can parse it with preview as well
    }

    @Test
    public void nonSealedClass() {
        doTest("geometry/Square");
        java17p.parseResource("geometry/Square.java"); // make sure we can parse it with preview as well
    }

    @Test(expected = ParseException.class)
    public void sealedInterfaceBeforeJava17() {
        java16.parseResource("expression/Expr.java");
    }

    @Test
    public void sealedInterface() {
        doTest("expression/Expr");
        java17p.parseResource("expression/Expr.java"); // make sure we can parse it with preview as well
    }

}
