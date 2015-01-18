/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import java.io.IOException;

import net.sourceforge.pmd.testframework.AbstractTokenizerTest;
import net.sourceforge.pmd.testframework.StreamUtil;

import org.junit.Before;
import org.junit.Test;


public class PythonTokenizerTest extends AbstractTokenizerTest {

    private static final String FILENAME = "sample-python.py";

    @Before
    @Override
    public void buildTokenizer() {
        this.tokenizer = new PythonTokenizer();
        this.sourceCode = new SourceCode(new SourceCode.StringCodeLoader(this.getSampleCode(), FILENAME));
    }

    @Override
    public String getSampleCode() {
         return StreamUtil.toString(PythonTokenizer.class.getResourceAsStream(FILENAME));
     }

    @Test
    public void tokenizeTest() throws IOException {
        this.expectedTokenCount = 1218;
        super.tokenizeTest();
    }
}
