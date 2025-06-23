/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.List;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.java.BaseJavaTreeDumpTest;
import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.java.types.OverloadSelectionResult;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.lang.test.ast.BaseParsingHelper;

class Java24PreviewTreeDumpTest extends BaseJavaTreeDumpTest {
    private final JavaParsingHelper java24p =
            JavaParsingHelper.DEFAULT.withDefaultVersion("24-preview")
                    .withResourceContext(Java24PreviewTreeDumpTest.class, "jdkversiontests/java24p/");
    private final JavaParsingHelper java24 = java24p.withDefaultVersion("24");

    @Override
    public BaseParsingHelper<?, ?> getParser() {
        return java24p;
    }

    @Test
    void jep488PrimitiveTypesInPatternsInstanceofAndSwitch() {
        doTest("Jep488_PrimitiveTypesInPatternsInstanceofAndSwitch");
    }

    @Test
    void jep488PrimitiveTypesInPatternsInstanceofAndSwitchBeforeJava24Preview() {
        ParseException thrown = assertThrows(ParseException.class, () -> java24.parseResource("Jep488_PrimitiveTypesInPatternsInstanceofAndSwitch.java"));
        assertThat(thrown.getMessage(), containsString("Primitive types in patterns instanceof and switch is a preview feature of JDK 24, you should select your language version accordingly"));
    }

    @Test
    void jep492FlexibleConstructorBodies() {
        doTest("Jep492_FlexibleConstructorBodies");
    }

    @Test
    void jep492FlexibleConstructorBodiesBeforeJava24Preview() {
        ParseException thrown = assertThrows(ParseException.class, () -> java24.parseResource("Jep492_FlexibleConstructorBodies.java"));
        assertThat(thrown.getMessage(), containsString("Flexible constructor bodies is a preview feature of JDK 24, you should select your language version accordingly"));
    }

    @Test
    void jep494ModuleImportDeclarations() {
        doTest("Jep494_ModuleImportDeclarations");
    }

    @Test
    void jep494ModuleImportDeclarationsBeforeJava24Preview() {
        ParseException thrown = assertThrows(ParseException.class, () -> java24.parseResource("Jep494_ModuleImportDeclarations.java"));
        assertThat(thrown.getMessage(), containsString("Module import declarations is a preview feature of JDK 24, you should select your language version accordingly"));
    }

    @Test
    void jep495SimpleSourceFilesAndInstanceMainMethods() {
        doTest("Jep495_SimpleSourceFilesAndInstanceMainMethods");
    }

    @Test
    void jep495SimpleSourceFilesAndInstanceMainMethodsVerifyTypes() {
        int javaVersion = Integer.parseInt(System.getProperty("java.version").split("\\.")[0].replaceAll("-ea", ""));
        assumeTrue(javaVersion >= 23, "Java " + javaVersion + " doesn't support java.io.IO. At least Java 23 is needed for this test.");

        ASTCompilationUnit compilationUnit = java24p.parseResource("Jep495_SimpleSourceFilesAndInstanceMainMethods.java");
        assertTrue(compilationUnit.isSimpleCompilationUnit());

        List<ASTMethodCall> methodCalls = compilationUnit.descendants(ASTMethodCall.class).toList();
        OverloadSelectionResult systemOutPrintln = methodCalls.get(4).getOverloadSelectionInfo(); // System.out.println
        assertFalse(systemOutPrintln.isFailed());
        TypeTestUtil.isA("java.io.PrintStream", systemOutPrintln.getMethodType().getDeclaringType());

        ASTVariableDeclarator authorsVar = compilationUnit.descendants(ASTVariableDeclarator.class).filter(decl -> "authors".equals(decl.getName())).first();
        assertInstanceOf(ASTMethodCall.class, authorsVar.getInitializer());
        ASTMethodCall initializer = (ASTMethodCall) authorsVar.getInitializer();
        assertEquals("of", initializer.getMethodName());
        assertInstanceOf(ASTTypeExpression.class, initializer.getQualifier());
        ASTTypeExpression qualifier = (ASTTypeExpression) initializer.getQualifier();
        TypeTestUtil.isA("java.util.List", qualifier.getTypeNode().getTypeMirror());

        OverloadSelectionResult javaIoPrintln = methodCalls.get(5).getOverloadSelectionInfo(); // println from java.io.IO
        assertFalse(javaIoPrintln.isFailed());
        TypeTestUtil.isA("java.io.IO", javaIoPrintln.getMethodType().getDeclaringType());
    }

    @Test
    void jep495SimpleSourceFilesAndInstanceMainMethodsBeforeJava24Preview() {
        ParseException thrown = assertThrows(ParseException.class, () -> java24.parseResource("Jep495_SimpleSourceFilesAndInstanceMainMethods.java"));
        assertThat(thrown.getMessage(), containsString("Simple source files and instance main methods is a preview feature of JDK 24, you should select your language version accordingly"));
    }
}
