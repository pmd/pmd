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
package test.net.sourceforge.pmd.ast;

import junit.framework.TestCase;
import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.ast.JavaParser;
import net.sourceforge.pmd.ast.ParseException;

import java.io.StringReader;

public class AssertTest extends TestCase  {

    public void testAssertAsKeywordVariantsSucceedWith1_4() {
        new JavaParser(new StringReader(TEST1)).CompilationUnit();
        new JavaParser(new StringReader(TEST2)).CompilationUnit();
        new JavaParser(new StringReader(TEST3)).CompilationUnit();
        new JavaParser(new StringReader(TEST4)).CompilationUnit();
    }

    public void testAssertAsVariableDeclIdentifierFailsWith1_4() {
        try {
            new JavaParser(new StringReader(TEST5)).CompilationUnit();
            throw new RuntimeException("Usage of assert as identifier should have failed with 1.4");
        } catch (ParseException pe) {
            // cool
        }
    }

    public void testAssertAsMethodNameIdentifierFailsWith1_4() {
        try {
            new JavaParser(new StringReader(TEST7)).CompilationUnit();
            throw new RuntimeException("Usage of assert as identifier should have failed with 1.4");
        } catch (ParseException pe) {
            // cool
        }
    }

    public void testAssertAsIdentifierSucceedsWith1_3() {
        JavaParser jp = new JavaParser(new StringReader(TEST5));
        jp.setAssertAsIdentifier();
        jp.CompilationUnit();
    }

    public void testAssertAsKeywordFailsWith1_3() {
        try {
            JavaParser jp = new JavaParser(new StringReader(TEST6));
            jp.setAssertAsIdentifier();
            jp.CompilationUnit();
            throw new RuntimeException("Usage of assert as keyword should have failed with 1.3");
        } catch (ParseException pe) {
            // cool
        }
    }

    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    " void bar() {" + PMD.EOL +
    "  assert x>2;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    " void bar() {" + PMD.EOL +
    "  assert (x>2);" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class Foo {" + PMD.EOL +
    " void bar() {" + PMD.EOL +
    "  assert x>2 : \"hi!\";" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST4 =
    "public class Foo {" + PMD.EOL +
    " void bar() {" + PMD.EOL +
    "  assert (x>2) : \"hi!\";" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST5 =
    "public class Foo {" + PMD.EOL +
    "  int assert = 2;" + PMD.EOL +
    "}";

    private static final String TEST6 =
    "public class Foo {" + PMD.EOL +
    " void foo() {" + PMD.EOL +
    "  assert (x>2) : \"hi!\";" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST7 =
    "public class Foo {" + PMD.EOL +
    " void assert() {}" + PMD.EOL +
    "}";


}
