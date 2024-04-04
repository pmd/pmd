/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.cs.cpd;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.cpd.CpdLanguageProperties;
import net.sourceforge.pmd.lang.ast.LexException;
import net.sourceforge.pmd.lang.test.cpd.CpdTextComparisonTest;
import net.sourceforge.pmd.lang.test.cpd.LanguagePropertyConfig;

class CsCpdLexerTest extends CpdTextComparisonTest {

    CsCpdLexerTest() {
        super("cs", ".cs");
    }

    @Test
    void testSimpleClass() {
        doTest("simpleClass");
    }

    @Test
    void testSimpleClassMethodMultipleLines() {
        doTest("simpleClassMethodMultipleLines");
    }

    @Test
    void testStrings() {
        doTest("strings");
    }

    @Test
    void testOpenString() {
        assertThrows(LexException.class, () -> doTest("unlexable_string"));
    }

    @Test
    void testCommentsIgnored1() {
        doTest("comments");
    }

    @Test
    void testIgnoreBetweenSpecialComments() {
        doTest("specialComments");
    }

    @Test
    void testOperators() {
        doTest("operatorsAndStuff");
    }


    @Test
    void testLineNumberAfterMultilineString() {
        doTest("strings");
    }

    @Test
    void testDoNotIgnoreUsingDirectives() {
        doTest("usingDirectives");
    }

    @Test
    void testIgnoreUsingDirectives() {
        doTest("usingDirectives", "_ignored", ignoreUsings());
    }

    @Test
    void testTabWidth() {
        doTest("tabWidth");
    }

    @Test
    void testLongListsOfNumbersAreNotIgnored() {
        doTest("listOfNumbers");
    }

    @Test
    void testLongListsOfNumbersAreIgnored() {
        doTest("listOfNumbers", "_ignored", skipLiteralSequences());
    }

    @Test
    void testCSharp7And8Additions() {
        doTest("csharp7And8Additions");
    }

    @Test
    void testAttributesAreNotIgnored() {
        doTest("attributes");
    }

    @Test
    void testAttributesAreIgnored() {
        doTest("attributes", "_ignored", skipAttributes());
    }

    private LanguagePropertyConfig ignoreUsings() {
        return properties(true, false, false);
    }

    private LanguagePropertyConfig skipLiteralSequences() {
        return properties(false, true, false);
    }

    private LanguagePropertyConfig skipAttributes() {
        return properties(false, false, true);
    }

    @Override
    public @NonNull LanguagePropertyConfig defaultProperties() {
        return properties(false, false, false);
    }

    private LanguagePropertyConfig properties(boolean ignoreUsings, boolean ignoreLiteralSequences, boolean ignoreAttributes) {
        return properties -> {
            properties.setProperty(CpdLanguageProperties.CPD_IGNORE_IMPORTS, ignoreUsings);
            properties.setProperty(CpdLanguageProperties.CPD_IGNORE_LITERAL_SEQUENCES, ignoreLiteralSequences);
            properties.setProperty(CpdLanguageProperties.CPD_IGNORE_METADATA, ignoreAttributes);
        };
    }
}
