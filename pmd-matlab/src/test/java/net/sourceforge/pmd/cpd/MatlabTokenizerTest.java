/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.testframework.AbstractTokenizerTest;

public class MatlabTokenizerTest extends AbstractTokenizerTest {

    private static final String FILENAME = "sample-matlab.m";

    @Before
    @Override
    public void buildTokenizer() throws IOException {
        this.tokenizer = new MatlabTokenizer();
        this.sourceCode = new SourceCode(new SourceCode.StringCodeLoader(this.getSampleCode(), FILENAME));
    }

    @Override
    public String getSampleCode() throws IOException {
        return IOUtils.toString(MatlabTokenizer.class.getResourceAsStream(FILENAME), StandardCharsets.UTF_8);
    }

    @Test
    public void tokenizeTest() throws IOException {
        this.expectedTokenCount = 3925;
        super.tokenizeTest();
    }
    
    @Test
    public void testIgnoreBetweenSpecialComments() throws IOException {
        SourceCode sourceCode = new SourceCode(new SourceCode.StringCodeLoader("% CPD-OFF" + PMD.EOL
                + "function g = vec(op, y)" + PMD.EOL
                + "  opy = op(y);" + PMD.EOL
                + "  if ( any(size(opy) > 1) )" + PMD.EOL
                + "    g = @loopWrapperArray;" + PMD.EOL
                + "  end" + PMD.EOL
                + "  % CPD-ON" + PMD.EOL
                + "end"
        ));
        Tokens tokens = new Tokens();
        tokenizer.tokenize(sourceCode, tokens);
        TokenEntry.getEOF();
        assertEquals(2, tokens.size()); // 2 tokens: "end" + EOF
    }
}
