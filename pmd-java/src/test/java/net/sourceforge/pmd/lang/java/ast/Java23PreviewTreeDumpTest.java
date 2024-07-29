/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.java.BaseJavaTreeDumpTest;
import net.sourceforge.pmd.lang.java.JavaParsingHelper;
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
        ASTMethodCall methodCall = compilationUnit.descendants(ASTMethodCall.class).first();
        assertNotNull(methodCall.getTypeMirror());
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
