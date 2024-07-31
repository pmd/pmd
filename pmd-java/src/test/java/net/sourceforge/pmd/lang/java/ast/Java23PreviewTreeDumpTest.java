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

class Java23PreviewTreeDumpTest extends BaseJavaTreeDumpTest {
    private final JavaParsingHelper java23p =
            JavaParsingHelper.DEFAULT.withDefaultVersion("23-preview")
                    .withResourceContext(Java22PreviewTreeDumpTest.class, "jdkversiontests/java23p/");
    private final JavaParsingHelper java23 = java23p.withDefaultVersion("23");

    @Override
    public BaseParsingHelper<?, ?> getParser() {
        return java23p;
    }

    @Test
    void jep477ImplicitlyDeclaredClassesAndInstanceMainMethods1() {
        doTest("Jep477_ImplicitlyDeclaredClassesAndInstanceMainMethods1");
        ASTCompilationUnit compilationUnit = java23p.parseResource("Jep477_ImplicitlyDeclaredClassesAndInstanceMainMethods1.java");
        assertTrue(compilationUnit.isSimpleCompilationUnit());

        List<ASTMethodCall> methodCalls = compilationUnit.descendants(ASTMethodCall.class).toList();
        OverloadSelectionResult systemOutPrintln = methodCalls.get(0).getOverloadSelectionInfo(); // System.out.println
        assertFalse(systemOutPrintln.isFailed());
        TypeTestUtil.isA("java.io.PrintStream", systemOutPrintln.getMethodType().getDeclaringType());

        ASTVariableDeclarator authorsVar = compilationUnit.descendants(ASTVariableDeclarator.class).filter(decl -> "authors".equals(decl.getName())).first();
        assertInstanceOf(ASTMethodCall.class, authorsVar.getInitializer());
        ASTMethodCall initializer = (ASTMethodCall) authorsVar.getInitializer();
        assertEquals("of", initializer.getMethodName());
        assertInstanceOf(ASTTypeExpression.class, initializer.getQualifier());
        ASTTypeExpression qualifier = (ASTTypeExpression) initializer.getQualifier();
        TypeTestUtil.isA("java.util.List", qualifier.getTypeNode().getTypeMirror());
    }

    @Test
    void jep477ImplicitlyDeclaredClassesAndInstanceMainMethods1WithJava23Runtime() {
        int javaVersion = Integer.parseInt(System.getProperty("java.version").split("\\.")[0].replaceAll("-ea", ""));
        assumeTrue(javaVersion >= 23, "Java " + javaVersion + " doesn't support java.io.IO. At least Java 23 is needed for this test.");

        ASTCompilationUnit compilationUnit = java23p.parseResource("Jep477_ImplicitlyDeclaredClassesAndInstanceMainMethods1.java");

        List<ASTMethodCall> methodCalls = compilationUnit.descendants(ASTMethodCall.class).toList();
        OverloadSelectionResult javaIoPrintln = methodCalls.get(1).getOverloadSelectionInfo(); // println from java.io.IO
        assertFalse(javaIoPrintln.isFailed());
        TypeTestUtil.isA("java.io.IO", javaIoPrintln.getMethodType().getDeclaringType());
    }

    @Test
    void jep477ImplicitlyDeclaredClassesAndInstanceMainMethods1BeforeJava23Preview() {
        ParseException thrown = assertThrows(ParseException.class, () -> java23.parseResource("Jep477_ImplicitlyDeclaredClassesAndInstanceMainMethods1.java"));
        assertThat(thrown.getMessage(), containsString("Implicitly declared classes and instance main methods is a preview feature of JDK 23, you should select your language version accordingly"));
    }

    @Test
    void jep477ImplicitlyDeclaredClassesAndInstanceMainMethods2() {
        doTest("Jep477_ImplicitlyDeclaredClassesAndInstanceMainMethods2");
    }

    @Test
    void jep482FlexibleConstructorBodies() {
        doTest("Jep482_FlexibleConstructorBodies");
    }

    @Test
    void jep482FlexibleConstructorBodiesBeforeJava23Preview() {
        ParseException thrown = assertThrows(ParseException.class, () -> java23.parseResource("Jep482_FlexibleConstructorBodies.java"));
        assertThat(thrown.getMessage(), containsString("Flexible constructor bodies is a preview feature of JDK 23, you should select your language version accordingly"));
    }

    @Test
    void jep476ModuleImportDeclarations() {
        doTest("Jep476_ModuleImportDeclarations");
    }

    @Test
    void jep476ModuleImportDeclarationsBeforeJava23Preview() {
        ParseException thrown = assertThrows(ParseException.class, () -> java23.parseResource("Jep476_ModuleImportDeclarations.java"));
        assertThat(thrown.getMessage(), containsString("Module import declarations is a preview feature of JDK 23, you should select your language version accordingly"));
    }

    @Test
    void jep455PrimitiveTypesInPatternsInstanceofAndSwitch() {
        doTest("Jep455_PrimitiveTypesInPatternsInstanceofAndSwitch");
    }

    @Test
    void jep455PrimitiveTypesInPatternsInstanceofAndSwitchBeforeJava23Preview() {
        ParseException thrown = assertThrows(ParseException.class, () -> java23.parseResource("Jep455_PrimitiveTypesInPatternsInstanceofAndSwitch.java"));
        assertThat(thrown.getMessage(), containsString("Primitive types in patterns instanceof and switch is a preview feature of JDK 23, you should select your language version accordingly"));
    }

    @Test
    void stringTemplatesAreNotSupportedAnymore() {
        ParseException thrown = assertThrows(ParseException.class, () -> java23p.parseResource("StringTemplatesAreNotSupportedAnymore.java"));
        assertThat(thrown.getMessage(), containsString("String templates is a preview feature of JDK 22, you should select your language version accordingly"));
        ParseException thrown2 = assertThrows(ParseException.class, () -> java23.parseResource("StringTemplatesAreNotSupportedAnymore.java"));
        assertThat(thrown2.getMessage(), containsString("String templates is a preview feature of JDK 22, you should select your language version accordingly"));
    }
}
