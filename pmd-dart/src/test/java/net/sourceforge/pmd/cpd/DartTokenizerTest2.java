/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import org.junit.Test;

import net.sourceforge.pmd.cpd.test.CpdTextComparisonTest;

public class DartTokenizerTest2 extends CpdTextComparisonTest {

    public DartTokenizerTest2() {
        super(".dart");
    }

    @Override
    protected String getResourcePrefix() {
        return "";
    }

    @Override
    public Tokenizer newTokenizer() {
        return new DartTokenizer();
    }

    @Test
    public void testComment() {
        doTest("comment");
    }


    @Test
    public void testMultiline() {
        doTest("string_multiline");
    }

    @Test
    public void testStringWithBackslashes() {
        doTest("string_with_backslashes");
    }

    @Test
    public void testIncrement() {
        doTest("increment");
    }



}
