package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.rules.StringToStringRule;

public class StringToStringRuleTest extends RuleTst {

    public void testLocalVar() throws Throwable {
        runTestFromFile("StringToString1.java", 1, new StringToStringRule());
    }

    public void testParam() throws Throwable {
        runTestFromFile("StringToString2.java", 1, new StringToStringRule());
    }

    public void testInstanceVar() throws Throwable {
        runTestFromFile("StringToString3.java", 1, new StringToStringRule());
    }

    public void testPrimitiveType() throws Throwable {
        runTestFromFile("StringToString4.java", 0, new StringToStringRule());
    }

    public void testMultipleSimilarParams() throws Throwable {
        runTestFromFile("StringToString5.java", 0, new StringToStringRule());
    }

    public void testStringArray() throws Throwable {
        runTestFromFile("StringToString6.java", 1, new StringToStringRule());
    }
}
