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
public class DartTokenizerTest extends AbstractTokenizerTest {

    private final String filename;
    private final int nExpectedTokens;

    public DartTokenizerTest(String filename, int nExpectedTokens) {
        this.filename = filename;
        this.nExpectedTokens = nExpectedTokens;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return Arrays.asList(
                new Object[] { "comment.dart", 5 },
                new Object[] { "escape_sequences.dart", 13 },
                new Object[] { "escaped_backslash.dart", 14 },
                new Object[] { "escaped_string.dart", 17 },
                new Object[] { "increment.dart", 185 },
                new Object[] { "imports.dart", 1 },
                new Object[] { "regex.dart", 13 },
                new Object[] { "regex2.dart", 13 },
                new Object[] { "regex3.dart", 13 },
                new Object[] { "string_with_backslashes.dart", 9 },
                new Object[] { "string_multiline.dart", 13 }
        );
    }

    @Before
    @Override
    public void buildTokenizer() throws IOException {
        this.tokenizer = new DartTokenizer();
        this.sourceCode = new SourceCode(new SourceCode.StringCodeLoader(this.getSampleCode(), this.filename));
    }

    @Override
    public String getSampleCode() throws IOException {
        return IOUtils.toString(DartTokenizer.class.getResourceAsStream(this.filename), StandardCharsets.UTF_8);
    }

    @Test
    public void tokenizeTest() throws IOException {
        this.expectedTokenCount = nExpectedTokens;
        super.tokenizeTest();
    }
}
