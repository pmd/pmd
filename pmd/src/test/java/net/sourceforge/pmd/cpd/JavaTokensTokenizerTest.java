/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cpd;

import static org.junit.Assert.assertEquals;
import net.sourceforge.pmd.PMD;

import org.junit.Test;

public class JavaTokensTokenizerTest {

    @Test
    public void test1() throws Throwable {
        Tokenizer tokenizer = new JavaTokenizer();
        SourceCode sourceCode = new SourceCode(new SourceCode.StringCodeLoader("public class Foo {}"));
        Tokens tokens = new Tokens();
        tokenizer.tokenize(sourceCode, tokens);
        assertEquals(6, tokens.size());
        assertEquals("public class Foo {}", sourceCode.getSlice(1, 1));
    }

    @Test
    public void testCommentsIgnored() throws Throwable {
        Tokenizer tokenizer = new JavaTokenizer();
        SourceCode sourceCode = new SourceCode(new SourceCode.StringCodeLoader("public class Foo { // class Bar */ \n }"));
        Tokens tokens = new Tokens();
        tokenizer.tokenize(sourceCode, tokens);
        assertEquals(6, tokens.size());
    }

    @Test
    public void test2() throws Throwable {
        Tokenizer t = new JavaTokenizer();
        String data = "public class Foo {" + PMD.EOL + "public void bar() {}" + PMD.EOL + "public void buz() {}" + PMD.EOL + "}";
        SourceCode sourceCode = new SourceCode(new SourceCode.StringCodeLoader(data));
        Tokens tokens = new Tokens();
        t.tokenize(sourceCode, tokens);
        assertEquals("public class Foo {" + PMD.EOL + "public void bar() {}", sourceCode.getSlice(1, 2));
    }

    @Test
    public void testDiscardSemicolons() throws Throwable {
        Tokenizer t = new JavaTokenizer();
        SourceCode sourceCode = new SourceCode(new SourceCode.StringCodeLoader("public class Foo {private int x;}"));
        Tokens tokens = new Tokens();
        t.tokenize(sourceCode, tokens);
        assertEquals(9, tokens.size());
    }

    @Test
    public void testDiscardImports() throws Throwable {
        Tokenizer t = new JavaTokenizer();
        SourceCode sourceCode = new SourceCode(new SourceCode.StringCodeLoader("import java.io.File;" + PMD.EOL + "public class Foo {}"));
        Tokens tokens = new Tokens();
        t.tokenize(sourceCode, tokens);
        assertEquals(6, tokens.size());
    }

    @Test
    public void testDiscardPkgStmts() throws Throwable {
        Tokenizer t = new JavaTokenizer();
        SourceCode sourceCode = new SourceCode(new SourceCode.StringCodeLoader("package foo.bar.baz;" + PMD.EOL + "public class Foo {}"));
        Tokens tokens = new Tokens();
        t.tokenize(sourceCode, tokens);
        assertEquals(6, tokens.size());
    }

    @Test
    public void testDiscardSimpleOneLineAnnotation() throws Throwable {
        JavaTokenizer t = new JavaTokenizer();
        t.setIgnoreAnnotations(true);
        SourceCode sourceCode = new SourceCode(
                new SourceCode.StringCodeLoader(
                    "package foo.bar.baz;" +
                    PMD.EOL +
                    "@MyAnnotation" +
                    PMD.EOL +
                    "public class Foo {}"
                ));
        Tokens tokens = new Tokens();
        t.tokenize(sourceCode, tokens);
        assertEquals(6, tokens.size());
    }

    /**
     * Comments are discarded already by the Java parser.
     * It would be nice, however, to use simple comments like
     * //CPD-START or //CPD-END
     * to enable discard-mode of CPD
     */
    @Test
    public void testIgnoreComments() {
        JavaTokenizer t = new JavaTokenizer();
        t.setIgnoreAnnotations(false);
        SourceCode sourceCode = new SourceCode(
                new SourceCode.StringCodeLoader(
                    "package foo.bar.baz;" +
                    PMD.EOL +
                    "/*****" +
                    PMD.EOL +
                    " * ugh" +
                    PMD.EOL +
                    " *****/" +
                    PMD.EOL +
                    "public class Foo {}"
                ));
        Tokens tokens = new Tokens();
        t.tokenize(sourceCode, tokens);
        assertEquals(6, tokens.size());
    }

    @Test
    public void testDiscardOneLineAnnotationWithParams() throws Throwable {
        JavaTokenizer t = new JavaTokenizer();
        t.setIgnoreAnnotations(true);

        SourceCode sourceCode = new SourceCode(
                new SourceCode.StringCodeLoader(
                    "package foo.bar.baz;" +
                    PMD.EOL +
                    "@ MyAnnotation (\"ugh\")" +
                    PMD.EOL +
                    "@NamedQueries({" +
                            PMD.EOL +
                    "@NamedQuery(" +
                            PMD.EOL +
                    ")})" +
                            PMD.EOL +
                    "public class Foo {" +
                            PMD.EOL +
                            "}"
                ));
        Tokens tokens = new Tokens();
        t.tokenize(sourceCode, tokens);
        TokenEntry.getEOF();
        assertEquals(6, tokens.size());
    }

    @Test
    public void testIgnoreBetweenSpecialAnnotation() throws Throwable {
        JavaTokenizer t = new JavaTokenizer();
        t.setIgnoreAnnotations(false);
        SourceCode sourceCode = new SourceCode(
                new SourceCode.StringCodeLoader(
                    "package foo.bar.baz;" +
                    PMD.EOL +
                    "@SuppressWarnings({\"woof\",\"CPD-START\"})" +
                    PMD.EOL +
                    "@SuppressWarnings(\"CPD-START\")" +
                    PMD.EOL +

                    "@ MyAnnotation (\"ugh\")" +
                    PMD.EOL +
                    "@NamedQueries({" +
                            PMD.EOL +
                    "@NamedQuery(" +
                            PMD.EOL +
                    ")})" +
                            PMD.EOL +
                    "public class Foo {}" +
                    "@SuppressWarnings({\"ugh\",\"CPD-END\"})" +
                    PMD.EOL

                ));
        Tokens tokens = new Tokens();
        t.tokenize(sourceCode, tokens);
        TokenEntry.getEOF();
        assertEquals(10, tokens.size());
    }


    @Test
    public void testIgnoreBetweenSpecialAnnotationAndIgnoreAnnotations() throws Throwable {
        JavaTokenizer t = new JavaTokenizer();
        t.setIgnoreAnnotations(true);
        SourceCode sourceCode = new SourceCode(
                new SourceCode.StringCodeLoader(
                    "package foo.bar.baz;" +
                    PMD.EOL +
                    "@SuppressWarnings({\"woof\",\"CPD-START\"})" +
                    PMD.EOL +
                    "@SuppressWarnings(\"CPD-START\")" +
                    PMD.EOL +

                    "@ MyAnnotation (\"ugh\")" +
                    PMD.EOL +
                    "@NamedQueries({" +
                            PMD.EOL +
                    "@NamedQuery(" +
                            PMD.EOL +
                    ")})" +
                            PMD.EOL +
                    "public class Foo {}" +
                            PMD.EOL +
                    "@SuppressWarnings({\"ugh\",\"CPD-END\"})" +
                    PMD.EOL

                ));
        Tokens tokens = new Tokens();
        t.tokenize(sourceCode, tokens);
        TokenEntry.getEOF();
        assertEquals(1, tokens.size());
    }


    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(JavaTokensTokenizerTest.class);
    }
}


