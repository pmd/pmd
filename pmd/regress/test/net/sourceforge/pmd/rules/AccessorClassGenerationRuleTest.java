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
import net.sourceforge.pmd.rules.AccessorClassGenerationRule;

public class AccessorClassGenerationRuleTest extends RuleTst {

    public void testInnerClassHasPrivateConstructor() throws Throwable {
        runTestFromString(TEST1, 1, new AccessorClassGenerationRule());
    }

    public void testInnerClassHasPublicConstructor() throws Throwable {
        runTestFromString(TEST2, 0, new AccessorClassGenerationRule());
    }

    public void testOuterClassHasPrivateConstructor() throws Throwable {
        runTestFromString(TEST3, 1, new AccessorClassGenerationRule());
    }

    public void testFinalInnerClass() throws Throwable {
        runTestFromString(TEST4, 0, new AccessorClassGenerationRule());
    }

    private static final String TEST1 =
    "public class Foo1 {" + PMD.EOL +
    " public class InnerClass {" + PMD.EOL +
    "   private InnerClass(){" + PMD.EOL +
    "   }" + PMD.EOL +
    " }" + PMD.EOL +
    " void method(){" + PMD.EOL +
    "   new InnerClass();//Causes generation of accessor" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST2 =
    "public class Foo2 {" + PMD.EOL +
    " public class InnerClass {" + PMD.EOL +
    "   public InnerClass(){" + PMD.EOL +
    "   }" + PMD.EOL +
    " }" + PMD.EOL +
    " void method(){" + PMD.EOL +
    "   new InnerClass(); //OK, due to public constructor" + PMD.EOL +
    " }" + PMD.EOL +
    "}";

    private static final String TEST3 =
    "public class Foo3 {" + PMD.EOL +
    "    public class InnerClass {" + PMD.EOL +
    "      void method(){" + PMD.EOL +
    "        new Foo3();//Causes generation of accessor" + PMD.EOL +
    "      }" + PMD.EOL +
    "    }" + PMD.EOL +
    "    private Foo3(){" + PMD.EOL +
    "    }" + PMD.EOL +
    "}";

    private static final String TEST4 =
    "public class Foo {" + PMD.EOL +
    " void method() {" + PMD.EOL +
    "   final class Inner {}; " + PMD.EOL +
    "   Inner i = new Inner();" + PMD.EOL +
    " }" + PMD.EOL +
    "}";
}
