/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import java.io.IOException;

import net.sourceforge.pmd.testframework.AbstractTokenizerTest;
import net.sourceforge.pmd.testframework.StreamUtil;

import org.junit.Before;
import org.junit.Test;


public class ObjectiveCTokenizerTest extends AbstractTokenizerTest {

    private static final String FILENAME = "AFHTTPRequestOperation.m";

    @Before
    @Override
    public void buildTokenizer() {
        this.tokenizer = new ObjectiveCTokenizer();
        this.sourceCode = new SourceCode(new SourceCode.StringCodeLoader(this.getSampleCode(), FILENAME));
    }

    @Override
    public String getSampleCode() {
         return StreamUtil.toString(ObjectiveCTokenizer.class.getResourceAsStream(FILENAME));
     }

    @Test
    public void tokenizeTest() throws IOException {
        this.expectedTokenCount = 884;
        super.tokenizeTest();
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(ObjectiveCTokenizerTest.class);
    }
}
