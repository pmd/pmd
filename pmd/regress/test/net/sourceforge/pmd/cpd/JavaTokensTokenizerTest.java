/**
 * <copyright>
 *  Copyright 1997-2002 InfoEther, LLC
 *  under sponsorship of the Defense Advanced Research Projects Agency
(DARPA).
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the Cougaar Open Source License as published
by
 *  DARPA on the Cougaar Open Source Website (www.cougaar.org).
 *
 *  THE COUGAAR SOFTWARE AND ANY DERIVATIVE SUPPLIED BY LICENSOR IS
 *  PROVIDED 'AS IS' WITHOUT WARRANTIES OF ANY KIND, WHETHER EXPRESS OR
 *  IMPLIED, INCLUDING (BUT NOT LIMITED TO) ALL IMPLIED WARRANTIES OF
 *  MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE, AND WITHOUT
 *  ANY WARRANTIES AS TO NON-INFRINGEMENT.  IN NO EVENT SHALL COPYRIGHT
 *  HOLDER BE LIABLE FOR ANY DIRECT, SPECIAL, INDIRECT OR CONSEQUENTIAL
 *  DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE OF DATA OR PROFITS,
 *  TORTIOUS CONDUCT, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *  PERFORMANCE OF THE COUGAAR SOFTWARE.
 * </copyright>
 */
package test.net.sourceforge.pmd.cpd;

import junit.framework.TestCase;
import net.sourceforge.pmd.cpd.JavaTokenizer;
import net.sourceforge.pmd.cpd.SourceCode;
import net.sourceforge.pmd.cpd.Tokenizer;
import net.sourceforge.pmd.cpd.Tokens;

import java.io.StringReader;

public class JavaTokensTokenizerTest extends TestCase {

    private static final String EOL = System.getProperty("line.separator", "\n");

    public void test1() throws Throwable {
        Tokenizer tokenizer = new JavaTokenizer();
        SourceCode sourceCode = new SourceCode("1");
        String data = "public class Foo {}";
        Tokens tokens = new Tokens();
        tokenizer.tokenize(sourceCode, tokens, new StringReader(data));
        assertEquals(6, tokens.size());
        assertEquals("public class Foo {}", sourceCode.getSlice(0, 0));
    }

    public void test2() throws Throwable {
        Tokenizer t = new JavaTokenizer();
        SourceCode sourceCode = new SourceCode("1");
        String data = "public class Foo {" + EOL + "public void bar() {}" + EOL + "public void buz() {}" + EOL + "}";
        Tokens tokens = new Tokens();
        t.tokenize(sourceCode, tokens, new StringReader(data));
        assertEquals("public class Foo {" + EOL + "public void bar() {}", sourceCode.getSlice(0, 1));
    }

    public void testDiscardSemicolons() throws Throwable {
        Tokenizer t = new JavaTokenizer();
        SourceCode sourceCode = new SourceCode("1");
        String data = "public class Foo {private int x;}";
        Tokens tokens = new Tokens();
        t.tokenize(sourceCode, tokens, new StringReader(data));
        assertEquals(9, tokens.size());
    }

    public void testDiscardImports() throws Throwable {
        Tokenizer t = new JavaTokenizer();
        SourceCode sourceCode = new SourceCode("1");
        String data = "import java.io.File;" + EOL + "public class Foo {}";
        Tokens tokens = new Tokens();
        t.tokenize(sourceCode, tokens, new StringReader(data));
        assertEquals(6, tokens.size());
    }

    public void testDiscardPkgStmts() throws Throwable {
        Tokenizer t = new JavaTokenizer();
        SourceCode sourceCode = new SourceCode("1");
        String data = "package foo.bar.baz;" + EOL + "public class Foo {}";
        Tokens tokens = new Tokens();
        t.tokenize(sourceCode, tokens, new StringReader(data));
        assertEquals(6, tokens.size());
    }
}


