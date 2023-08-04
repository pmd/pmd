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

class Java21PreviewTreeDumpTest extends BaseTreeDumpTest {
    private final JavaParsingHelper java21p =
            JavaParsingHelper.DEFAULT.withDefaultVersion("21-preview")
                    .withResourceContext(Java21PreviewTreeDumpTest.class, "jdkversiontests/java21p/");
    private final JavaParsingHelper java21 = java21p.withDefaultVersion("21");

    Java21PreviewTreeDumpTest() {
        super(new RelevantAttributePrinter(), ".java");
    }

    @Override
    public BaseParsingHelper<?, ?> getParser() {
        return java21p;
    }

    @Test
    void recordPatterns() {
        doTest("RecordPatterns");
    }

    @Test
    void recordPatternsBeforeJava21Preview() {
        ParseException thrown = assertThrows(ParseException.class, () -> java21.parseResource("RecordPatterns.java"));
        assertTrue(thrown.getMessage().contains("Deconstruction patterns is a preview feature of JDK 20, you should select your language version accordingly"),
                "Unexpected message: " + thrown.getMessage());
    }

    @Test
    void recordPatternsInEnhancedFor() {
        doTest("RecordPatternsInEnhancedFor");
    }

    @Test
    void recordPatternsInEnhancedForBeforeJava21Preview() {
        ParseException thrown = assertThrows(ParseException.class, () -> java21.parseResource("RecordPatternsInEnhancedFor.java"));
        assertTrue(thrown.getMessage().contains("Deconstruction patterns in enhanced for statement is a preview feature of JDK 20, you should select your language version accordingly"),
                "Unexpected message: " + thrown.getMessage());
    }

    @Test
    void recordPatternsExhaustiveSwitch() {
        doTest("RecordPatternsExhaustiveSwitch");
    }
}
