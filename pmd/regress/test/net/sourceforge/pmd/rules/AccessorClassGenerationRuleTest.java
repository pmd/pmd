package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.rules.AccessorClassGenerationRule;
import net.sourceforge.pmd.cpd.CPD;

public class AccessorClassGenerationRuleTest extends RuleTst {

    private static final String TEST1 =
    "public class AccessorClassGeneration1 {" + CPD.EOL +
    " public class InnerClass {" + CPD.EOL +
    "   private InnerClass(){" + CPD.EOL +
    "   }" + CPD.EOL +
    " }" + CPD.EOL +
    " void method(){" + CPD.EOL +
    "   new InnerClass();//Causes generation of accessor" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST2 =
    "public class AccessorClassGeneration2 {" + CPD.EOL +
    " public class InnerClass {" + CPD.EOL +
    "   public InnerClass(){" + CPD.EOL +
    "   }" + CPD.EOL +
    " }" + CPD.EOL +
    " void method(){" + CPD.EOL +
    "   new InnerClass(); //OK, due to public constructor" + CPD.EOL +
    " }" + CPD.EOL +
    "}";

    private static final String TEST3 =
    "public class AccessorClassGeneration3 {" + CPD.EOL +
    "    public class InnerClass {" + CPD.EOL +
    "      void method(){" + CPD.EOL +
    "        new AccessorClassGeneration3();//Causes generation of accessor" + CPD.EOL +
    "      }" + CPD.EOL +
    "    }" + CPD.EOL +
    "    private AccessorClassGeneration3(){" + CPD.EOL +
    "    }" + CPD.EOL +
    "}";

    public void testInnerClassHasPrivateConstructor() throws Throwable {
        runTestFromString(TEST1, 1, new AccessorClassGenerationRule());
    }

    public void testInnerClassHasPublicConstructor() throws Throwable {
        runTestFromString(TEST2, 0, new AccessorClassGenerationRule());
    }

    public void testOuterClassHasPrivateConstructor() throws Throwable {
        runTestFromString(TEST3, 1, new AccessorClassGenerationRule());
    }
}
