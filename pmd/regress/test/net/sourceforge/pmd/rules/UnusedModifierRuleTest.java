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
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.rules.XPathRule;

public class UnusedModifierRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() {
        rule = new XPathRule();
        rule.addProperty("xpath", "//InterfaceDeclaration//MethodDeclaration[@Public='true' or @Abstract = 'true']");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "wasted 'public'", 1, rule),
           new TestDescriptor(TEST2, "class, no problem", 0, rule),
           new TestDescriptor(TEST3, "wasted 'abstract'", 1, rule),
           new TestDescriptor(TEST4, "all is well", 0, rule)
       });
    }

    private static final String TEST1 =
    "public interface Foo {" + PMD.EOL +
    " public void bar();" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public abstract class Foo {" + PMD.EOL +
    " public abstract void bar();" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public interface Foo {" + PMD.EOL +
    " abstract void bar();" + PMD.EOL +
    "}";

    private static final String TEST4 =
    "public interface Foo {" + PMD.EOL +
    " void bar();" + PMD.EOL +
    "}";

}
