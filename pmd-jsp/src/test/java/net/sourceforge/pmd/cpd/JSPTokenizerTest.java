/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.lang.jsp.ast.JspParserConstants;


public class JSPTokenizerTest {

    @Test
    public void scriptletWithString() throws Exception {
        JSPTokenizer tokenizer = new JSPTokenizer();
        Tokens tokenEntries = new Tokens();
        String code = IOUtils.toString(JSPTokenizerTest.class.getResourceAsStream("scriptletWithString.jsp"),
                StandardCharsets.UTF_8);
        SourceCode sourceCode = new SourceCode(new SourceCode.StringCodeLoader(code));
        tokenizer.tokenize(sourceCode, tokenEntries);

        int[] expectedTokens = new int[] {
            JspParserConstants.JSP_COMMENT_START,
            JspParserConstants.JSP_COMMENT_CONTENT,
            JspParserConstants.JSP_COMMENT_END,
            JspParserConstants.JSP_SCRIPTLET_START,
            JspParserConstants.JSP_SCRIPTLET,
            JspParserConstants.JSP_SCRIPTLET_END,
            JspParserConstants.JSP_SCRIPTLET_START,
            JspParserConstants.JSP_SCRIPTLET,
            JspParserConstants.JSP_SCRIPTLET_END,
            JspParserConstants.EOF,
        };
        Assert.assertEquals(expectedTokens.length, tokenEntries.getTokens().size());
        for (int i = 0; i < expectedTokens.length - 1; i++) {
            Assert.assertEquals(String.valueOf(expectedTokens[i]), tokenEntries.getTokens().get(i).toString());
        }
        Assert.assertEquals("<JSP_SCRIPTLET>", getTokenImage(tokenEntries.getTokens().get(4)));
    }

    private static String getTokenImage(TokenEntry token) {
        int kind = 0;
        if (token != TokenEntry.EOF) {
            kind = Integer.parseInt(token.toString());
        }
        return JspParserConstants.tokenImage[kind];
    }
}
