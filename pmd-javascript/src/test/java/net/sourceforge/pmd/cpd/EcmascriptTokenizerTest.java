/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.io.IOException;
import java.util.List;

import net.sourceforge.pmd.PMD;

import org.junit.Test;

public class EcmascriptTokenizerTest {

    @Test
    public void test1() throws Throwable {
        Tokenizer tokenizer = new EcmascriptTokenizer();
        SourceCode sourceCode = new SourceCode( new SourceCode.StringCodeLoader( getCode1() ) );
        Tokens tokens = new Tokens();
        tokenizer.tokenize( sourceCode, tokens );
        assertEquals( 22, tokens.size() );
    }

    @Test
    public void test2() throws Throwable {
        Tokenizer t = new EcmascriptTokenizer();
        SourceCode sourceCode = new SourceCode( new SourceCode.StringCodeLoader( getCode2() ) );
        Tokens tokens = new Tokens();
        t.tokenize( sourceCode, tokens );
        assertEquals( 22, tokens.size() );
    }

    /**
     * See: https://sourceforge.net/p/pmd/bugs/1239/
     * @throws IOException IO Exception
     */
    @Test
    public void parseStringNotAsMultiline() throws IOException {
        Tokenizer t = new EcmascriptTokenizer();
        SourceCode sourceCode = new SourceCode( new SourceCode.StringCodeLoader(
                  "var s = \"a string \\\n"
                + "continues\";\n"
                + "var s = \"a string \\\n"
                + "continues2\";\n") );
        Tokens tokens = new Tokens();
        t.tokenize(sourceCode, tokens);
        assertEquals(9, tokens.size());
        List<TokenEntry> list = tokens.getTokens();
        assertEquals("var", list.get(0).getIdentifier(), list.get(4).getIdentifier());
        assertEquals("s", list.get(1).getIdentifier(), list.get(5).getIdentifier());
        assertEquals("=", list.get(2).getIdentifier(), list.get(6).getIdentifier());
        assertEquals("\"a string continues\"", list.get(3).toString());
        assertEquals("\"a string continues2\"", list.get(7).toString());
        assertFalse(list.get(3).getIdentifier() == list.get(7).getIdentifier());
    }

    // no semi-colons
    private String getCode1() {
        StringBuilder sb = new StringBuilder();
        sb.append( "function switchToRealPassword() {" ).append(PMD.EOL);
        sb.append( "   var real = $('realPass')" ).append(PMD.EOL);
        sb.append( "   var prompt = $('promptPass')" ).append(PMD.EOL);
        sb.append( "   real.style.display = 'inline'" ).append(PMD.EOL);
        sb.append( "   prompt.style.display = 'none'" ).append(PMD.EOL);
        sb.append( "   real.focus()" ).append(PMD.EOL);
        sb.append( "}" ).append(PMD.EOL);
        return sb.toString();
    }

    // same as getCode1, but lines are ended with semi-colons
    private String getCode2() {
        StringBuilder sb = new StringBuilder();
        sb.append( "function switchToRealPassword() {" ).append(PMD.EOL);
        sb.append( "   var real = $('realPass');" ).append(PMD.EOL);
        sb.append( "   var prompt = $('promptPass');" ).append(PMD.EOL);
        sb.append( "   real.style.display = 'inline';" ).append(PMD.EOL);
        sb.append( "   prompt.style.display = 'none';" ).append(PMD.EOL);
        sb.append( "   real.focus();" ).append(PMD.EOL);
        sb.append( "}" ).append(PMD.EOL);
        return sb.toString();
    }
}