/*
 * User: tom
 * Date: Oct 10, 2002
 * Time: 3:26:45 PM
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.rules.StringToStringRule;

public class StringToStringRuleTest extends RuleTst {

    public void testLocalVar() throws Throwable {
        runTest("StringToString1.java", 1, new StringToStringRule());
    }

    public void testParam() throws Throwable {
        runTest("StringToString2.java", 1, new StringToStringRule());
    }

    public void testInstanceVar() throws Throwable {
        runTest("StringToString3.java", 1, new StringToStringRule());
    }
    public void testPrimitiveType() throws Throwable {
        runTest("StringToString4.java", 0, new StringToStringRule());
    }
    public void testMultipleSimilarParams() throws Throwable {
        runTest("StringToString5.java", 0, new StringToStringRule());
    }
    public void testStringArray() throws Throwable {
        runTest("StringToString6.java", 1, new StringToStringRule());
    }
}
