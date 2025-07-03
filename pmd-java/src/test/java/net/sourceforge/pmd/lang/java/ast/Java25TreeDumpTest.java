/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.java.BaseJavaTreeDumpTest;
import net.sourceforge.pmd.lang.java.JavaParsingHelper;
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
        assertThat(thrown.getMessage(), containsString("Flexible constructor bodies was only standardized in Java 25, you should select your language version accordingly"));
    }

    @Test
    void jep511ModuleImportDeclarations() {
        doTest("Jep511_ModuleImportDeclarations");
    }

    @Test
    void jep511ModuleImportDeclarationsBeforeJava25() {
        ParseException thrown = assertThrows(ParseException.class, () -> java24.parseResource("Jep511_ModuleImportDeclarations.java"));
        assertThat(thrown.getMessage(), containsString("Module import declarations was only standardized in Java 25, you should select your language version accordingly"));
    }

}
