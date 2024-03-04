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

class Java21TreeDumpTest extends BaseJavaTreeDumpTest {
    private final JavaParsingHelper java21 =
            JavaParsingHelper.DEFAULT.withDefaultVersion("21")
                    .withResourceContext(Java21TreeDumpTest.class, "jdkversiontests/java21/");
    private final JavaParsingHelper java20 = java21.withDefaultVersion("20");

    @Override
    public BaseParsingHelper<?, ?> getParser() {
        return java21;
    }

    @Test
    void patternMatchingForSwitch() {
        doTest("Jep441_PatternMatchingForSwitch");
    }

    @Test
    void patternMatchingForSwitchBeforeJava21() {
        ParseException thrown = assertThrows(ParseException.class, () -> java20.parseResource("Jep441_PatternMatchingForSwitch.java"));
        assertThat(thrown.getMessage(), containsString("Patterns in switch statements are a feature of Java 21, you should select your language version accordingly"));
    }

    @Test
    void dealingWithNull() {
        doTest("DealingWithNull");
    }

    @Test
    void dealingWithNullBeforeJava21() {
        ParseException thrown = assertThrows(ParseException.class, () -> java20.parseResource("DealingWithNull.java"));
        assertThat(thrown.getMessage(), containsString("Null in switch cases are a feature of Java 21, you should select your language version accordingly"));
    }


    @Test
    void enhancedTypeCheckingSwitch() {
        doTest("EnhancedTypeCheckingSwitch");
    }

    @Test
    void exhaustiveSwitch() {
        doTest("ExhaustiveSwitch");
    }

    @Test
    void guardedPatterns() {
        doTest("GuardedPatterns");
    }

    @Test
    void guardedPatternsBeforeJava21() {
        ParseException thrown = assertThrows(ParseException.class, () -> java20.parseResource("GuardedPatterns.java"));
        assertThat(thrown.getMessage(), containsString("Patterns in switch statements are a feature of Java 21, you should select your language version accordingly"));
    }

    @Test
    void patternsInSwitchLabels() {
        doTest("PatternsInSwitchLabels");
    }

    @Test
    void patternsInSwitchLabelsBeforeJava21() {
        ParseException thrown = assertThrows(ParseException.class, () -> java20.parseResource("PatternsInSwitchLabels.java"));
        assertThat(thrown.getMessage(), containsString("Patterns in switch statements are a feature of Java 21, you should select your language version accordingly"));
    }

    @Test
    void refiningPatternsInSwitch() {
        doTest("RefiningPatternsInSwitch");
    }

    @Test
    void scopeOfPatternVariableDeclarations() {
        doTest("ScopeOfPatternVariableDeclarations");
    }

    @Test
    void recordPatternsJep() {
        doTest("Jep440_RecordPatterns");
    }

    @Test
    void recordPatternsJepBeforeJava21() {
        ParseException thrown = assertThrows(ParseException.class, () -> java20.parseResource("Jep440_RecordPatterns.java"));
        assertThat(thrown.getMessage(), containsString("Record patterns are a feature of Java 21, you should select your language version accordingly"));
    }

    @Test
    void recordPatterns() {
        doTest("RecordPatterns");
    }

    @Test
    void recordPatternsBeforeJava21() {
        ParseException thrown = assertThrows(ParseException.class, () -> java20.parseResource("RecordPatterns.java"));
        assertThat(thrown.getMessage(), containsString("Record patterns are a feature of Java 21, you should select your language version accordingly"));
    }

    @Test
    void recordPatternsExhaustiveSwitch() {
        doTest("RecordPatternsExhaustiveSwitch");
    }

    @Test
    void canParseAnnotationValueInitializers() {
        doTest("AnnotationValueInitializers");
    }
}
