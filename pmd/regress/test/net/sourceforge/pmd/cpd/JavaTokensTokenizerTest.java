/*
 * User: tom
 * Date: Aug 6, 2002
 * Time: 9:05:19 AM
 */
package test.net.sourceforge.pmd.cpd;

import junit.framework.TestCase;
import net.sourceforge.pmd.cpd.JavaTokensTokenizer;
import net.sourceforge.pmd.cpd.TokenList;
import net.sourceforge.pmd.cpd.Tokenizer;

import java.io.StringReader;

public class JavaTokensTokenizerTest extends TestCase {

    private static final String EOL = System.getProperty("line.separator", "\n");

    public void test1() throws Throwable {
        Tokenizer tokenizer = new JavaTokensTokenizer();
        TokenList tl = new TokenList("1");
        String data = "public class Foo {}";
        tokenizer.tokenize(tl, new StringReader(data));
        assertEquals(5, tl.size());
        assertEquals("public class Foo {}", tl.getSlice(0,0));
    }

    public void test2() throws Throwable {
        Tokenizer t = new JavaTokensTokenizer();
        TokenList tl = new TokenList("1");
        String data = "public class Foo {" + EOL +
                      "public void bar() {}" + EOL +
                      "public void buz() {}" + EOL +
                      "}";
        t.tokenize(tl, new StringReader(data));
        assertEquals("public class Foo {"+ EOL + "public void bar() {}", tl.getSlice(0,1));
    }

    public void testDiscardSemicolons() throws Throwable  {
        Tokenizer t = new JavaTokensTokenizer();
        TokenList tl = new TokenList("1");
        String data = "public class Foo {private int x;}";
        t.tokenize(tl, new StringReader(data));
        assertEquals(8, tl.size());
    }

    public void testDiscardImports() throws Throwable {
        Tokenizer t = new JavaTokensTokenizer();
        TokenList tl = new TokenList("1");
        String data = "import java.io.File;" + EOL + "public class Foo {}";
        t.tokenize(tl, new StringReader(data));
        assertEquals(5, tl.size());
    }

    public void testDiscardPkgStmts() throws Throwable {
        Tokenizer t = new JavaTokensTokenizer();
        TokenList tl = new TokenList("1");
        String data = "package foo.bar.baz;" + EOL + "public class Foo {}";
        t.tokenize(tl, new StringReader(data));
        assertEquals(5, tl.size());
    }
 }


