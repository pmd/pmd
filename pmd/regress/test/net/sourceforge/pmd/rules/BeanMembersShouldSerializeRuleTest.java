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
import net.sourceforge.pmd.rules.BeanMembersShouldSerializeRule;

public class BeanMembersShouldSerializeRuleTest extends SimpleAggregatorTst {

    private Rule rule;

    public void setUp() {
        rule = new BeanMembersShouldSerializeRule();
        rule.setMessage("Don't {0} !");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "private String, no accessor", 1, rule),
           new TestDescriptor(TEST2, "private static String", 0, rule),
           new TestDescriptor(TEST3, "private transient String", 0, rule),
           new TestDescriptor(TEST4, "getter, no setter", 1, rule),
           new TestDescriptor(TEST5, "setter, no getter", 1, rule),
           new TestDescriptor(TEST6, "both accessors, yay!", 0, rule),
       });
    }

    private static final String TEST1 =
    "public class Foo {" + PMD.EOL +
    " private String foo;" + PMD.EOL +
    " private String bar = foo;" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo {" + PMD.EOL +
    " private static String foo;" + PMD.EOL +
    " private String bar = Foo.foo;" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class Foo {" + PMD.EOL +
    " private transient String foo;" + PMD.EOL +
    " private String bar = Foo.foo;" + PMD.EOL +
    "}";

    private static final String TEST4 =
    "public class Foo {" + PMD.EOL +
    " private String foo;" + PMD.EOL +
    " private String bar = Foo.foo;" + PMD.EOL +
    " public String getFoo() {return foo;}" + PMD.EOL +
    "}";

    private static final String TEST5 =
    "public class Foo {" + PMD.EOL +
    " private String foo;" + PMD.EOL +
    " private String bar = Foo.foo;" + PMD.EOL +
    " public void setFoo(Foo foo) {this.foo = foo;}" + PMD.EOL +
    "}";

    private static final String TEST6 =
    "public class Foo {" + PMD.EOL +
    " private String foo;" + PMD.EOL +
    " private String bar = Foo.foo;" + PMD.EOL +
    " public void setFoo(Foo foo) {this.foo = foo;}" + PMD.EOL +
    " public String getFoo() {return foo;}" + PMD.EOL +
    "}";

}
