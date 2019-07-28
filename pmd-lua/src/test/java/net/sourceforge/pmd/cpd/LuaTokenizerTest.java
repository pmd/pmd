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
public class LuaTokenizerTest extends AbstractTokenizerTest {

    private final String filename;
    private final int nExpectedTokens;

    public LuaTokenizerTest(String filename, int nExpectedTokens) {
        this.filename = filename;
        this.nExpectedTokens = nExpectedTokens;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(
                new Object[] { "factorial.lua", 44 },
                new Object[] { "helloworld.lua", 5 }
        );
    }

    @Before
    @Override
    public void buildTokenizer() throws IOException {
        this.tokenizer = new LuaTokenizer();
        this.sourceCode = new SourceCode(new SourceCode.StringCodeLoader(this.getSampleCode(), this.filename));
    }

    @Override
    public String getSampleCode() throws IOException {
        return IOUtils.toString(LuaTokenizer.class.getResourceAsStream(this.filename), StandardCharsets.UTF_8);
    }

    @Test
    public void tokenizeTest() throws IOException {
        this.expectedTokenCount = nExpectedTokens;
        super.tokenizeTest();
    }
}
