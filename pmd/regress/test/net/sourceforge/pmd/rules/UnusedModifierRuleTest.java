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
        rule.addProperty("xpath", "//UnmodifiedInterfaceDeclaration//MethodDeclaration[@Public='true' or @Abstract = 'true']" +
                              "|   //UnmodifiedInterfaceDeclaration//FieldDeclaration[@Public = 'true' or @Static = 'true' or @Final = 'true']" +
                              "|   //UnmodifiedInterfaceDeclaration//NestedClassDeclaration[@Public = 'true' or @Static = 'true']" +
                              "|   //UnmodifiedInterfaceDeclaration//NestedInterfaceDeclaration[@Public = 'true' or @Static = 'true']" +
                              "|   //UnmodifiedClassDeclaration//NestedInterfaceDeclaration[@Static = 'true']");
    }

    public void testAll() {
       runTests(new TestDescriptor[] {
           new TestDescriptor(TEST1, "wasted 'public' in interface method", 1, rule),
           new TestDescriptor(TEST2, "class, no problem", 0, rule),
           new TestDescriptor(TEST3, "wasted 'abstract' in interface method", 1, rule),
           new TestDescriptor(TEST4, "all is well in interface method", 0, rule),
           new TestDescriptor(TEST5, "wasted 'public' in interface field", 1, rule),
           new TestDescriptor(TEST6, "wasted 'static' in interface field", 1, rule),
           new TestDescriptor(TEST7, "wasted 'final' in interface field", 1, rule),
           new TestDescriptor(TEST8, "wasted 'public static final' in interface field", 1, rule),
           new TestDescriptor(TEST9, "OK in interface field", 0, rule),
           new TestDescriptor(TEST10, "wasted 'public' in class nested in interface", 1, rule),
           new TestDescriptor(TEST11, "wasted 'static' in class nested in interface", 1, rule),
           new TestDescriptor(TEST12, "OK in class nested in interface", 0, rule),
           new TestDescriptor(TEST13, "wasted 'public' in interface nested in interface", 1, rule),
           new TestDescriptor(TEST14, "wasted 'static' in interface nested in interface", 1, rule),
           new TestDescriptor(TEST15, "OK in interface nested in interface", 0, rule),
           new TestDescriptor(TEST16, "wasted 'static' in interface nested in class", 1, rule),
           new TestDescriptor(TEST17, "OK in interface nested in class", 0, rule),
           new TestDescriptor(TEST18, "wasted 'public static final' in interface field inside another interface", 2, rule),
           new TestDescriptor(TEST19, "OK in interface field inside another interface", 0, rule),
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

    private static final String TEST5 =
    "public interface Foo {" + PMD.EOL +
    " public int X = 0;" + PMD.EOL +
    "}";

    private static final String TEST6 =
    "public interface Foo {" + PMD.EOL +
    " static int X = 0;" + PMD.EOL +
    "}";

    private static final String TEST7 =
    "public interface Foo {" + PMD.EOL +
    " final int X = 0;" + PMD.EOL +
    "}";

    private static final String TEST8 =
    "public interface Foo {" + PMD.EOL +
    " public static final int X = 0;" + PMD.EOL +
    "}";

    private static final String TEST9 =
    "public interface Foo {" + PMD.EOL +
    " int X = 0;" + PMD.EOL +
    "}";

    private static final String TEST10 =
    "public interface Foo {" + PMD.EOL +
    " public class Bar {}" + PMD.EOL +
    "}";

    private static final String TEST11 =
    "public interface Foo {" + PMD.EOL +
    " static class Bar {}" + PMD.EOL +
    "}";

    private static final String TEST12 =
    "public interface Foo {" + PMD.EOL +
    " class Bar {}" + PMD.EOL +
    "}";

    private static final String TEST13 =
    "public interface Foo {" + PMD.EOL +
    " public interface Baz {}" + PMD.EOL +
    "}";

    private static final String TEST14 =
    "public interface Foo {" + PMD.EOL +
    " static interface Baz {}" + PMD.EOL +
    "}";

    private static final String TEST15 =
    "public interface Foo {" + PMD.EOL +
    " interface Baz {}" + PMD.EOL +
    "}";

    private static final String TEST16 =
    "public class Foo {" + PMD.EOL +
    " public static interface Bar {}" + PMD.EOL +
    "}";

    private static final String TEST17 =
    "public class Foo {" + PMD.EOL +
    " public interface Bar {}" + PMD.EOL +
    "}";

    private static final String TEST18 =
    "public interface Foo {" + PMD.EOL +
    " public interface Bar {" + PMD.EOL +
    "  public static final int X = 0;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST19 =
    "interface Foo {" + PMD.EOL +
    " interface Bar {" + PMD.EOL +
    "  int X = 0;" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

}
