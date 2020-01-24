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

public class PLSQLTokenizerTest extends AbstractTokenizerTest {

    private static final String FILENAME = "sample-plsql.sql";

    @Before
    @Override
    public void buildTokenizer() throws IOException {
        this.tokenizer = new PLSQLTokenizer();
        this.sourceCode = new SourceCode(new SourceCode.StringCodeLoader(this.getSampleCode(), FILENAME));
    }

    @Override
    public String getSampleCode() throws IOException {
        return IOUtils.toString(PLSQLTokenizer.class.getResourceAsStream(FILENAME), StandardCharsets.UTF_8);
    }

    @Test
    public void tokenizeTest() throws IOException {
        this.expectedTokenCount = 1422;
        super.tokenizeTest();
    }

    @Test
    public void testIgnoreBetweenSpecialComments() throws IOException {
        SourceCode sourceCode = new SourceCode(new SourceCode.StringCodeLoader("-- CPD-OFF" + PMD.EOL
                + "CREATE OR REPLACE" + PMD.EOL
                + "PACKAGE \"test_schema\".\"BANK_DATA\"" + PMD.EOL
                + "IS" + PMD.EOL
                + "pi      CONSTANT NUMBER := 3.1415;" + PMD.EOL
                + "--CPD-ON" + PMD.EOL
                + "END;"
        ));
        Tokens tokens = new Tokens();
        tokenizer.tokenize(sourceCode, tokens);
        TokenEntry.getEOF();
        assertEquals(3, tokens.size()); // 3 tokens: "END" + ";" + EOF
    }
}
