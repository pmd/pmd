/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.util.Properties;

import org.junit.Test;

import net.sourceforge.pmd.cpd.test.CpdTextComparisonTest;

public class PythonTokenizerTest extends CpdTextComparisonTest {

    public PythonTokenizerTest() {
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
    public void sampleTest() {
        doTest("sample_python");
    }

    @Test
    public void specialComments() {
        doTest("special_comments");
    }

    @Test
    public void testBackticks() {
        doTest("backticks");
    }

    @Test
    public void testTabWidth() {
        doTest("tabWidth");
    }
}
