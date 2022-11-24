/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.Properties;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.cpd.test.CpdTextComparisonTest;

class PythonTokenizerTest extends CpdTextComparisonTest {

    PythonTokenizerTest() {
        super(".py");
    }

    @Override
    protected String getResourcePrefix() {
        return "../lang/python/cpd/testdata";
    }

    @Override
    public Tokenizer newTokenizer(Properties properties) {
        return new PythonTokenizer();
    }
    
    
    @Test
    void sampleTest() {
        doTest("sample_python");
    }

    @Test
    void specialComments() {
        doTest("special_comments");
    }

    @Test
    void testBackticks() {
        doTest("backticks");
    }

    @Test
    void testUnicode() {
        doTest("sample_unicode");
    }

    @Test
    void testTabWidth() {
        doTest("tabWidth");
    }

    @Test
    void testVarWithDollar() {
        doTest("var_with_dollar");
    }

}
