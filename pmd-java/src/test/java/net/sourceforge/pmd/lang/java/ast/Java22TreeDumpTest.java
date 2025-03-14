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

class Java22TreeDumpTest extends BaseJavaTreeDumpTest {
    private final JavaParsingHelper java22 =
            JavaParsingHelper.DEFAULT.withDefaultVersion("22")
                    .withResourceContext(Java21TreeDumpTest.class, "jdkversiontests/java22/");
    private final JavaParsingHelper java21 = java22.withDefaultVersion("21");
    private final JavaParsingHelper java17 = java22.withDefaultVersion("17");

    @Override
    public BaseParsingHelper<?, ?> getParser() {
        return java22;
    }

    @Test
    void jep456UnnamedPatternsAndVariables() {
        doTest("Jep456_UnnamedPatternsAndVariables");
    }

    @Test
    void jep456UnnamedPatternsAndVariablesBeforeJava22() {
        ParseException thrown = assertThrows(ParseException.class, () -> java21.parseResource("Jep456_UnnamedPatternsAndVariables.java"));
        assertThat(thrown.getMessage(), containsString("Unnamed variables and patterns are a feature of Java 22, you should select your language version accordingly"));
    }

    @Test
    void jep456UnnamedVariablesAndPatternsUnderscoreBeforeJava21() {
        ParseException thrown = assertThrows(ParseException.class, () -> java17.parse("class Test { { for(Integer _ : java.util.Arrays.asList(1, 2, 3)) {} } }"));
        assertThat(thrown.getMessage(), containsString("Since Java 9, '_' is reserved and cannot be used as an identifier"));
    }
}
