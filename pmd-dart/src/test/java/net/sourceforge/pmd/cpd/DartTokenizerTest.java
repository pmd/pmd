/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.Properties;

import org.junit.Test;

import net.sourceforge.pmd.cpd.test.CpdTextComparisonTest;

public class DartTokenizerTest extends CpdTextComparisonTest {

    public DartTokenizerTest() {
        super(".dart");
    }

    @Override
    public Tokenizer newTokenizer(Properties properties) {
        return new DartTokenizer();
    }


    @Test
    public void testComment() {
        doTest("comment");
    }

    @Test
    public void testEscapeSequences() {
        doTest("escape_sequences");
    }

    @Test
    public void testEscapedBackslash() {
        doTest("escaped_backslash");
    }

    @Test
    public void testEscapedString() {
        doTest("escaped_string");
    }


    @Test
    public void testIncrement() {
        doTest("increment");
    }


    @Test
    public void testImports() {
        doTest("imports");
    }

    @Test
    public void testStringInterpolation() {
        doTest("string_interpolation");
    }

    @Test
    public void testRegex() {
        doTest("regex");
    }


    @Test
    public void testRegex2() {
        doTest("regex2");
    }

    @Test
    public void testRegex3() {
        doTest("regex3");
    }

    @Test
    public void testStringWithBackslashes() {
        doTest("string_with_backslashes");
    }

    @Test
    public void testMultiline() {
        doTest("string_multiline");
    }

    @Test
    public void testTabWidth() {
        doTest("tabWidth");
    }

}
