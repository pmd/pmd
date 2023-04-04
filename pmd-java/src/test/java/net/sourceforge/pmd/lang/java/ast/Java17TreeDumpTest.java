/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.test.BaseParsingHelper;
import net.sourceforge.pmd.lang.ast.test.BaseTreeDumpTest;
import net.sourceforge.pmd.lang.ast.test.RelevantAttributePrinter;
import net.sourceforge.pmd.lang.java.JavaParsingHelper;

class Java17TreeDumpTest extends BaseTreeDumpTest {
    private final JavaParsingHelper java17 =
            JavaParsingHelper.DEFAULT.withDefaultVersion("17")
                                     .withResourceContext(Java17TreeDumpTest.class, "jdkversiontests/java17/");
    private final JavaParsingHelper java16 = java17.withDefaultVersion("16");

    Java17TreeDumpTest() {
        super(new RelevantAttributePrinter(), ".java");
    }

    @Override
    public BaseParsingHelper<?, ?> getParser() {
        return java17;
    }

    @Test
    void sealedClassBeforeJava17() {
        ParseException thrown = assertThrows(ParseException.class, () -> java16.parseResource("geometry/Shape.java"));
        assertTrue(thrown.getMessage().contains("Sealed classes are a feature of Java 17, you should select your language version accordingly"),
                "Unexpected message: " + thrown.getMessage());
    }

    @Test
    void sealedClass() {
        doTest("geometry/Shape");
    }

    @Test
    void nonSealedClass() {
        doTest("geometry/Square");
    }

    @Test
    void sealedQualifiedPermitClass() {
        doTest("SealedInnerClasses");
    }

    @Test
    void sealedInterfaceBeforeJava17() {
        ParseException thrown = assertThrows(ParseException.class, () -> java16.parseResource("expression/Expr.java"));
        assertTrue(thrown.getMessage().contains("Sealed classes are a feature of Java 17, you should select your language version accordingly"),
                "Unexpected message: " + thrown.getMessage());
    }

    @Test
    void sealedInterface() {
        doTest("expression/Expr");
    }

    @Test
    void localVars() {
        doTest("LocalVars");
    }
}
