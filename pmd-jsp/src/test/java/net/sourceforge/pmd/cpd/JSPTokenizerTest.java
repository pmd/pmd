/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

public class JSPTokenizerTest {

    @Test
    public void scriptletWithString() throws Exception {
        JSPTokenizer tokenizer = new JSPTokenizer();
        Tokens tokenEntries = new Tokens();
        String code = IOUtils.toString(JSPTokenizerTest.class.getResourceAsStream("scriptletWithString.jsp"),
                                       StandardCharsets.UTF_8);
        SourceCode sourceCode = new SourceCode(new SourceCode.StringCodeLoader(code));
        tokenizer.tokenize(sourceCode, tokenEntries);

        String[] expectedTokens = new String[] {
            "<%--",
            "\n"
                + "BSD-style license; for more info see http://pmd.sourceforge.net/license.html\n",
            "--%>",
            "<%",
            "\nString nodeContent = \"<% %>\";\n",
            "%>",
            "<%",
            "\n<![cdata[\n"
                + "String nodeContent = \"<% %>\";\n"
                + "]]>\n",
            "%>",
            "",
            };



        Assert.assertEquals(expectedTokens.length, tokenEntries.getTokens().size());
        for (int i = 0; i < expectedTokens.length - 1; i++) {
            TokenEntry tokenEntry = tokenEntries.getTokens().get(i);
            Assert.assertEquals(expectedTokens[i], tokenEntry.toString());
        }
    }

}
