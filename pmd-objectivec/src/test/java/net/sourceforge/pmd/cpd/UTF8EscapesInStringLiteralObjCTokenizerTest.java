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

//Tests if the ObjectiveC tokenizer supports UTF-8 escapes in string literals
public class UTF8EscapesInStringLiteralObjCTokenizerTest extends AbstractTokenizerTest {

    private static final String FILENAME = "FileWithUTF8EscapeInStringLiteral.m";

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
        this.expectedTokenCount = 45;
        super.tokenizeTest();
    }
}
