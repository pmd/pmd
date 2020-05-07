/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import net.sourceforge.pmd.testframework.AbstractTokenizerTest;

@RunWith(Parameterized.class)
public class SwiftTokenizerTest extends AbstractTokenizerTest {

    private final String filename;
    private final int nExpectedTokens;

    public SwiftTokenizerTest(String filename, int nExpectedTokens) {
        this.filename = filename;
        this.nExpectedTokens = nExpectedTokens;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(
                new Object[] { "Swift5.2.swift", 90 },
                new Object[] { "Swift5.1.swift", 242 },
                new Object[] { "Swift5.0.swift", 172 },
                new Object[] { "Swift4.2.swift", 91 },
                new Object[] { "BTree.swift", 4239 }
        );
    }

    @Before
    @Override
    public void buildTokenizer() throws IOException {
        this.tokenizer = new SwiftTokenizer();
        this.sourceCode = new SourceCode(new SourceCode.StringCodeLoader(this.getSampleCode(), this.filename));
    }

    @Override
    public String getSampleCode() throws IOException {
        return IOUtils.toString(SwiftTokenizer.class.getResourceAsStream(this.filename), StandardCharsets.UTF_8);
    }

    @Test
    public void tokenizeTest() throws IOException {
        this.expectedTokenCount = nExpectedTokens;
        super.tokenizeTest();
    }
}
