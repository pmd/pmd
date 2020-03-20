/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.lang.jsp.JspParserTest;

public class JSPTokenizerTest extends JspParserTest {

    @Test
    public void scriptletWithString() throws Exception {
        JSPTokenizer tokenizer = new JSPTokenizer();
        Tokens tokenEntries = new Tokens();
        String code = jsp.readResource("scriptletWithString.jsp");
        SourceCode sourceCode = new SourceCode(new SourceCode.StringCodeLoader(code));
        tokenizer.tokenize(sourceCode, tokenEntries);

        String[] expectedTokens = new String[] {
            "<%--",
            PMD.EOL + "BSD-style license; for more info see http://pmd.sourceforge.net/license.html" + PMD.EOL,
            "--%>",
            "<%",
            PMD.EOL + "String nodeContent = \"<% %>\";" + PMD.EOL,
            "%>",
            "<%",
            PMD.EOL + "<![cdata[" + PMD.EOL
                + "String nodeContent = \"<% %>\";" + PMD.EOL
                + "]]>" + PMD.EOL,
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
