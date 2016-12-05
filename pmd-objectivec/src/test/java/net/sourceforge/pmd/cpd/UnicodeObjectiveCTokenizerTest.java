/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.IOException;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;

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
        return IOUtils.toString(ObjectiveCTokenizer.class.getResourceAsStream(FILENAME), "UTF-8");
    }

    @Test
    public void tokenizeTest() throws IOException {
        this.expectedTokenCount = 10;
        super.tokenizeTest();
    }
}
