/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.PMD;
import net.sourceforge.pmd.rules.AccessorClassGenerationRule;
import test.net.sourceforge.pmd.testframework.RuleTst;

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
    "    private Foo3() {" + PMD.EOL +
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
