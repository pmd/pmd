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

/**
 * @author rpelisse
 *
 */
public class FortranTokenizerTest extends AbstractTokenizerTest {

    @Before
    @Override
    public void buildTokenizer() throws IOException {
        this.tokenizer = new FortranTokenizer();
        this.sourceCode = new SourceCode(new SourceCode.StringCodeLoader(this.getSampleCode(), "sample.for"));
    }

    @Override
    public String getSampleCode() throws IOException {
        return IOUtils.toString(FortranTokenizerTest.class.getResourceAsStream("sample.for"), StandardCharsets.UTF_8);
    }

    @Test
    public void tokenizeTest() throws IOException {
        this.expectedTokenCount = 434;
        super.tokenizeTest();
    }
}
