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

import net.sourceforge.pmd.testframework.AbstractTokenizerTest;

public class PythonTokenizerTest extends AbstractTokenizerTest {

    private static final String FILENAME = "sample-python.py";

    @Before
    @Override
    public void buildTokenizer() throws IOException {
        this.tokenizer = new PythonTokenizer();
        this.sourceCode = new SourceCode(new SourceCode.StringCodeLoader(this.getSampleCode(), FILENAME));
    }

    @Override
    public String getSampleCode() throws IOException {
        return IOUtils.toString(PythonTokenizer.class.getResourceAsStream(FILENAME), StandardCharsets.UTF_8);
    }

    @Test
    public void tokenizeTest() throws IOException {
        this.expectedTokenCount = 1218;
        super.tokenizeTest();
    }

    @Test
    public void testIgnoreBetweenSpecialComments() throws IOException {
        SourceCode sourceCode = new SourceCode(new SourceCode.StringCodeLoader("import logging\n"
                + "# CPD-OFF\n"
                + "logger = logging.getLogger('django.request')\n"
                + "class BaseHandler(object):\n"
                + "    def __init__(self):\n"
                + "        self._request_middleware = None\n"
                + "        # CPD-ON\n"
        ));
        Tokens tokens = new Tokens();
        tokenizer.tokenize(sourceCode, tokens);
        TokenEntry.getEOF();
        assertEquals(3, tokens.size()); // 3 tokens: "import" + "logging" + EOF
    }

    @Test
    public void testBackticks() throws IOException {
        SourceCode sourceCode = new SourceCode(new SourceCode.StringCodeLoader("test = 'hello'\n"
                + "quoted = `test`\n"
                + "print quoted\n"
        ));
        Tokens tokens = new Tokens();
        tokenizer.tokenize(sourceCode, tokens); // should not result in parse error
        TokenEntry.getEOF();
        assertEquals(3, tokens.getTokens().get(tokens.getTokens().size() - 2).getBeginLine());
    }
}
