/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.Properties;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.cpd.test.CpdTextComparisonTest;

class DartTokenizerTest extends CpdTextComparisonTest {

    DartTokenizerTest() {
        super(".dart");
    }

    @Override
    public Tokenizer newTokenizer(Properties properties) {
        return new DartTokenizer();
    }


    @Test
    void testComment() {
        doTest("comment");
    }

    @Test
    void testEscapeSequences() {
        doTest("escape_sequences");
    }

    @Test
    void testEscapedBackslash() {
        doTest("escaped_backslash");
    }

    @Test
    void testEscapedString() {
        doTest("escaped_string");
    }


    @Test
    void testIncrement() {
        doTest("increment");
    }


    @Test
    void testImports() {
        doTest("imports");
    }

    @Test
    void testStringInterpolation() {
        doTest("string_interpolation");
    }

    @Test
    void testEscapedDollar() {
        doTest("escaped_dollar");
    }

    @Test
    void testRegex() {
        doTest("regex");
    }


    @Test
    void testRegex2() {
        doTest("regex2");
    }

    @Test
    void testRegex3() {
        doTest("regex3");
    }

    @Test
    void testStringWithBackslashes() {
        doTest("string_with_backslashes");
    }

    @Test
    void testMultiline() {
        doTest("string_multiline");
    }

    @Test
    void testTabWidth() {
        doTest("tabWidth");
    }

}
