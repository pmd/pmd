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

//Tests if the ObjectiveC tokenizer supports identifiers with unicode characters
public class UnicodeObjectiveCTokenizerTest extends AbstractTokenizerTest {

    private static final String FILENAME = "NCClient.m";

    @Before
    @Override
    public void buildTokenizer() throws IOException {
        this.tokenizer = new ObjectiveCTokenizer();
        this.sourceCode = new SourceCode(new SourceCode.StringCodeLoader(this.getSampleCode(), FILENAME));
    }

    @Override
    public String getSampleCode() throws IOException {
        return IOUtils.toString(ObjectiveCTokenizer.class.getResourceAsStream(FILENAME), StandardCharsets.UTF_8);
    }

    @Test
    public void tokenizeTest() throws IOException {
        this.expectedTokenCount = 10;
        super.tokenizeTest();
    }

    @Test
    public void testIgnoreBetweenSpecialComments() throws IOException {
        SourceCode sourceCode = new SourceCode(new SourceCode.StringCodeLoader(
            "// CPD-OFF" + PMD.EOL
            + "static SecCertificateRef gNСServerLogonCertificate;" + PMD.EOL
            + "// CPD-ON" + PMD.EOL
            + "@end" + PMD.EOL
        ));
        Tokens tokens = new Tokens();
        tokenizer.tokenize(sourceCode, tokens);
        TokenEntry.getEOF();
        assertEquals(2, tokens.size()); // 2 tokens: "@end" + EOF
    }
}
