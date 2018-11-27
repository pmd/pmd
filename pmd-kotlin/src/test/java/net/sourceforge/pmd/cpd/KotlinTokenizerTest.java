/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import net.sourceforge.pmd.testframework.AbstractTokenizerTest;

@RunWith(Parameterized.class)
public class KotlinTokenizerTest extends AbstractTokenizerTest {

    private final String filename;
    private final int nExpectedTokens;

    public KotlinTokenizerTest(String filename, int nExpectedTokens) {
        this.filename = filename;
        this.nExpectedTokens = nExpectedTokens;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(
                new Object[] { "comment.kt", 5 },
                new Object[] { "increment.kt", 185 },
                new Object[] { "imports.kt", 1 }
        );
    }

    @Before
    @Override
    public void buildTokenizer() throws IOException {
        this.tokenizer = new KotlinTokenizer();
        this.sourceCode = new SourceCode(new SourceCode.StringCodeLoader(this.getSampleCode(), this.filename));
    }

    @Override
    public String getSampleCode() throws IOException {
        return IOUtils.toString(KotlinTokenizer.class.getResourceAsStream(this.filename));
    }

    @Test
    public void tokenizeTest() throws IOException {
        this.expectedTokenCount = nExpectedTokens;
        super.tokenizeTest();
    }
}
