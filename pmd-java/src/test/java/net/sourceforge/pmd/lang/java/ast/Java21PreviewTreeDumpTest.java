/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.java.BaseJavaTreeDumpTest;
import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.test.ast.BaseParsingHelper;

class Java21PreviewTreeDumpTest extends BaseJavaTreeDumpTest {
    private final JavaParsingHelper java21p =
            JavaParsingHelper.DEFAULT.withDefaultVersion("21-preview")
                    .withResourceContext(Java21PreviewTreeDumpTest.class, "jdkversiontests/java21p/");
    private final JavaParsingHelper java21 = java21p.withDefaultVersion("21");

    @Override
    public BaseParsingHelper<?, ?> getParser() {
        return java21p;
    }

    @Test
    void templateProcessors() {
        doTest("Jep430_StringTemplates");
    }

    @Test
    void templateProcessorsBeforeJava21Preview() {
        ParseException thrown = assertThrows(ParseException.class, () -> java21.parseResource("Jep430_StringTemplates.java"));
        assertTrue(thrown.getMessage().contains("String templates is a preview feature of JDK 21, you should select your language version accordingly"));
    }

    @Test
    void templateExpressionType() {
        ASTCompilationUnit unit = java21p.parse("class Foo {{ int i = 1; String s = STR.\"i = \\{i}\"; }}");
        ASTTemplateExpression templateExpression = unit.descendants(ASTTemplateExpression.class).first();
        JTypeMirror typeMirror = templateExpression.getTypeMirror();
        assertEquals("java.lang.String", ((JClassSymbol) typeMirror.getSymbol()).getCanonicalName());
    }

    @Test
    void unnamedPatternsAndVariables() {
        doTest("Jep443_UnnamedPatternsAndVariables");
    }

    @Test
    void unnamedPatternsAndVariablesBeforeJava21Preview() {
        ParseException thrown = assertThrows(ParseException.class, () -> java21.parseResource("Jep443_UnnamedPatternsAndVariables.java"));
        assertThat(thrown.getMessage(), containsString("Unnamed variables and patterns was only standardized in Java 22, you should select your language version accordingly"));
    }

    @Test
    void unnamedClasses1() {
        doTest("Jep445_UnnamedClasses1");
        ASTCompilationUnit compilationUnit = java21p.parseResource("Jep445_UnnamedClasses1.java");
        assertTrue(compilationUnit.isUnnamedClass());
        ASTMethodCall methodCall = compilationUnit.descendants(ASTMethodCall.class).first();
        assertNotNull(methodCall.getTypeMirror());
    }

    @Test
    void unnamedClasses2() {
        doTest("Jep445_UnnamedClasses2");
    }

    @Test
    void unnamedClasses3() {
        doTest("Jep445_UnnamedClasses3");
    }

    @Test
    void unnamedClassesBeforeJava21Preview() {
        ParseException thrown = assertThrows(ParseException.class, () -> java21.parseResource("Jep445_UnnamedClasses1.java"));
        assertThat(thrown.getMessage(), containsString("Unnamed classes is a preview feature of JDK 21, you should select your language version accordingly"));
    }

    @Test
    void testOrdinaryCompilationUnit() {
        ASTCompilationUnit compilationUnit = java21.parse("public class Foo { public static void main(String[] args) {}}");
        assertFalse(compilationUnit.isUnnamedClass());
    }

    @Test
    void testModularCompilationUnit() {
        ASTCompilationUnit compilationUnit = java21.parse("module foo {}");
        assertFalse(compilationUnit.isUnnamedClass());
    }
}
