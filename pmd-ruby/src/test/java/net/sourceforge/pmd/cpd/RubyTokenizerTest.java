/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

import net.sourceforge.pmd.testframework.AbstractTokenizerTest;

public class RubyTokenizerTest extends AbstractTokenizerTest {

    @Before
    @Override
    public void buildTokenizer() {
        this.tokenizer = new RubyTokenizer();
        this.sourceCode = new SourceCode(new SourceCode.StringCodeLoader(this.getSampleCode(), "server.rb"));
    }

    @Override
    public String getSampleCode() {
        try {
            return IOUtils.toString(RubyTokenizerTest.class.getResourceAsStream("server.rb"), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void tokenizeTest() throws IOException {
        this.expectedTokenCount = 30;
        super.tokenizeTest();
    }
}
