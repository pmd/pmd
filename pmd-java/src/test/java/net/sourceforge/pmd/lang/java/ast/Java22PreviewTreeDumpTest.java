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

class Java22PreviewTreeDumpTest extends BaseJavaTreeDumpTest {
    private final JavaParsingHelper java22p =
            JavaParsingHelper.DEFAULT.withDefaultVersion("22-preview")
                    .withResourceContext(Java22PreviewTreeDumpTest.class, "jdkversiontests/java22p/");
    private final JavaParsingHelper java22 = java22p.withDefaultVersion("22");

    @Override
    public BaseParsingHelper<?, ?> getParser() {
        return java22p;
    }

    @Test
    void jep459TemplateProcessors() {
        doTest("Jep459_StringTemplates");
    }

    @Test
    void jep459TemplateProcessorsBeforeJava22Preview() {
        ParseException thrown = assertThrows(ParseException.class, () -> java22.parseResource("Jep459_StringTemplates.java"));
        assertThat(thrown.getMessage(), containsString("String templates is a preview feature of JDK 22, you should select your language version accordingly"));
    }

    @Test
    void jep459TemplateExpressionType() {
        ASTCompilationUnit unit = java22p.parse("class Foo {{ int i = 1; String s = STR.\"i = \\{i}\"; }}");
        ASTTemplateExpression templateExpression = unit.descendants(ASTTemplateExpression.class).first();
        JTypeMirror typeMirror = templateExpression.getTypeMirror();
        assertEquals("java.lang.String", ((JClassSymbol) typeMirror.getSymbol()).getCanonicalName());
    }

    @Test
    void jep463UnnamedClasses1() {
        doTest("Jep463_UnnamedClasses1");
        ASTCompilationUnit compilationUnit = java22p.parseResource("Jep463_UnnamedClasses1.java");
        assertTrue(compilationUnit.isImplicitlyDeclaredClass());
        ASTMethodCall methodCall = compilationUnit.descendants(ASTMethodCall.class).first();
        assertNotNull(methodCall.getTypeMirror());
    }

    @Test
    void jep463UnnamedClasses2() {
        doTest("Jep463_UnnamedClasses2");
    }

    @Test
    void jep463UnnamedClasses3() {
        doTest("Jep463_UnnamedClasses3");
    }

    @Test
    void jep463UnnamedClasses4WithImports() {
        doTest("Jep463_UnnamedClasses4WithImports");
    }

    @Test
    void jep463UnnamedClassesBeforeJava22Preview() {
        ParseException thrown = assertThrows(ParseException.class, () -> java22.parseResource("Jep463_UnnamedClasses1.java"));
        assertThat(thrown.getMessage(), containsString("Implicitly declared classes and instance main methods is a preview feature of JDK 22, you should select your language version accordingly"));
    }

    @Test
    void jep463TestOrdinaryCompilationUnit() {
        ASTCompilationUnit compilationUnit = java22.parse("public class Foo { public static void main(String[] args) {}}");
        assertFalse(compilationUnit.isImplicitlyDeclaredClass());
    }

    @Test
    void jep463TestModularCompilationUnit() {
        ASTCompilationUnit compilationUnit = java22.parse("module foo {}");
        assertFalse(compilationUnit.isImplicitlyDeclaredClass());
    }

    @Test
    void jep447StatementsBeforeSuper() {
        doTest("Jep447_StatementsBeforeSuper");
    }

    @Test
    void jep447StatementsBeforeSuperBeforeJava22Preview() {
        ParseException thrown = assertThrows(ParseException.class, () -> java22.parseResource("Jep447_StatementsBeforeSuper.java"));
        assertThat(thrown.getMessage(), containsString("Statements before super is a preview feature of JDK 22, you should select your language version accordingly"));
    }
}
