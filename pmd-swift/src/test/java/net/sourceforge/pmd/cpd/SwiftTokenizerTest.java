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

public class SwiftTokenizerTest extends AbstractTokenizerTest {

    private static final String FILENAME = "BTree.swift";

    @Before
    @Override
    public void buildTokenizer() throws IOException {
        this.tokenizer = new SwiftTokenizer();
        this.sourceCode = new SourceCode(new SourceCode.StringCodeLoader(this.getSampleCode(), FILENAME));
    }

    @Override
    public String getSampleCode() throws IOException {
        return IOUtils.toString(SwiftTokenizer.class.getResourceAsStream(FILENAME), StandardCharsets.UTF_8);
    }

    @Test
    public void tokenizeTest() throws IOException {
        this.expectedTokenCount = 4239;
        super.tokenizeTest();
    }
}
