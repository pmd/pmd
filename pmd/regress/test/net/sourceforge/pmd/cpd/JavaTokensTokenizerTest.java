/*
 * User: tom
 * Date: Aug 6, 2002
 * Time: 9:05:19 AM
 */
package test.net.sourceforge.pmd.cpd;

import junit.framework.TestCase;
import net.sourceforge.pmd.cpd.CharacterTokenizer;
import net.sourceforge.pmd.cpd.TokenList;
import net.sourceforge.pmd.cpd.JavaTokensTokenizer;
import net.sourceforge.pmd.cpd.Tokenizer;

import java.io.StringReader;

public class JavaTokensTokenizerTest extends TestCase {
    public JavaTokensTokenizerTest(String name) {
        super(name);
    }

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
        String data = "public class Foo {}"
                + System.getProperty("line.separator")
                + "public void bar() {}"
                + System.getProperty("line.separator")
                + "public void buz() {}"
                + System.getProperty("line.separator")
                + "}";
        t.tokenize(tl, new StringReader(data));
        assertEquals("public class Foo {}"+ System.getProperty("line.separator") + "public void bar() {}", tl.getSlice(0,1));
    }

 }


