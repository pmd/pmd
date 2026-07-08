/*
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

class Java25TreeDumpTest extends BaseJavaTreeDumpTest {
    private final JavaParsingHelper java25 =
            JavaParsingHelper.DEFAULT.withDefaultVersion("25")
                    .withResourceContext(Java25TreeDumpTest.class, "jdkversiontests/java25/");
    private final JavaParsingHelper java24 = java25.withDefaultVersion("24");

    @Override
    public BaseParsingHelper<?, ?> getParser() {
        return java25;
    }

    @Test
    void jep513FlexibleConstructorBodies() {
        doTest("Jep513_FlexibleConstructorBodies");
    }

    @Test
    void jep513FlexibleConstructorBodiesBeforeJava25() {
        ParseException thrown = assertThrows(ParseException.class, () -> java24.parseResource("Jep513_FlexibleConstructorBodies.java"));
        assertThat(thrown.getMessage(), containsString("Flexible constructor bodies are a feature of Java 25, you should select your language version accordingly"));
    }

    @Test
    void jep511ModuleImportDeclarations() {
        doTest("Jep511_ModuleImportDeclarations");
    }

    @Test
    void jep511ModuleImportDeclarationsBeforeJava25() {
        ParseException thrown = assertThrows(ParseException.class, () -> java24.parseResource("Jep511_ModuleImportDeclarations.java"));
        assertThat(thrown.getMessage(), containsString("Module import declarations are a feature of Java 25, you should select your language version accordingly"));
    }

    @Test
    void jep512CompactSourceFilesAndInstanceMainMethods() {
        int javaVersion = Integer.parseInt(System.getProperty("java.version").split("\\.")[0].replaceAll("-ea", ""));
        //assumeTrue(javaVersion < 23, "This test is for Java < 23, where java.lang.IO/java.io.IO is not existing yet - and thus AmbiguousNames are left in the AST");
        if (javaVersion < 23) {
            // java.lang.IO/java.io.IO is not existing yet - and thus AmbiguousNames are left in the AST
            doTest("Jep512_CompactSourceFilesAndInstanceMainMethodsBeforeJava23");
        } else {
            // java.lang.IO/java.io.IO are available - the AmbiguousNames are resolved as ClassTypes in the AST
            doTest("Jep512_CompactSourceFilesAndInstanceMainMethodsAfterJava23");
        }
    }

    @Test
    void jep512CompactSourceFilesAndInstanceMainMethodsVerifyTypes() {
        int javaVersion = Integer.parseInt(System.getProperty("java.version").split("\\.")[0].replaceAll("-ea", ""));
        assumeTrue(javaVersion >= 25, "Java " + javaVersion + " doesn't support java.lang.IO. At least Java 25 is needed for this test.");

        ASTCompilationUnit compilationUnit = java25.parseResource("Jep512_CompactSourceFilesAndInstanceMainMethodsAfterJava23.java");
        assertTrue(compilationUnit.isCompact());

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

        OverloadSelectionResult javaIoPrintln = methodCalls.get(5).getOverloadSelectionInfo(); // println from java.lang.IO
        assertFalse(javaIoPrintln.isFailed());
        TypeTestUtil.isA("java.lang.IO", javaIoPrintln.getMethodType().getDeclaringType());
    }

    @Test
    void jep512CompactSourceFilesAndInstanceMainMethodsBeforeJava24Preview() {
        ParseException thrown = assertThrows(ParseException.class, () -> java24.parseResource("Jep512_CompactSourceFilesAndInstanceMainMethodsAfterJava23.java"));
        assertThat(thrown.getMessage(), containsString("Compact source files and instance main methods are a feature of Java 25, you should select your language version accordingly"));
    }

}
