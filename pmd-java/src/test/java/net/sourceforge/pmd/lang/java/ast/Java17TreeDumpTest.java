/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.junit.Assert;
import org.junit.Test;
import org.junit.function.ThrowingRunnable;

import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.test.BaseParsingHelper;
import net.sourceforge.pmd.lang.ast.test.BaseTreeDumpTest;
import net.sourceforge.pmd.lang.ast.test.RelevantAttributePrinter;
import net.sourceforge.pmd.lang.java.JavaParsingHelper;

public class Java17TreeDumpTest extends BaseTreeDumpTest {
    private final JavaParsingHelper java17 =
            JavaParsingHelper.DEFAULT.withDefaultVersion("17")
                                     .withResourceContext(Java17TreeDumpTest.class, "jdkversiontests/java17/");
    private final JavaParsingHelper java16 = java17.withDefaultVersion("16");

    public Java17TreeDumpTest() {
        super(new RelevantAttributePrinter(), ".java");
    }

    @Override
    public BaseParsingHelper<?, ?> getParser() {
        return java17;
    }

    @Test
    public void sealedClassBeforeJava17() {
        ParseException thrown = Assert.assertThrows(ParseException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                java16.parseResource("geometry/Shape.java");
            }
        });
        Assert.assertTrue("Unexpected message: " + thrown.getMessage(),
                thrown.getMessage().contains("Sealed classes are a feature of Java 17, you should select your language version accordingly"));
    }

    @Test
    public void sealedClass() {
        doTest("geometry/Shape");
    }

    @Test
    public void nonSealedClass() {
        doTest("geometry/Square");
    }

    @Test
    public void sealedQualifiedPermitClass() {
        doTest("SealedInnerClasses");
    }

    @Test
    public void sealedInterfaceBeforeJava17() {
        ParseException thrown = Assert.assertThrows(ParseException.class, new ThrowingRunnable() {
            @Override
            public void run() throws Throwable {
                java16.parseResource("expression/Expr.java");
            }
        });
        Assert.assertTrue("Unexpected message: " + thrown.getMessage(),
                thrown.getMessage().contains("Sealed classes are a feature of Java 17, you should select your language version accordingly"));
    }

    @Test
    public void sealedInterface() {
        doTest("expression/Expr");
    }

    @Test
    public void localVars() {
        doTest("LocalVars");
    }
}
