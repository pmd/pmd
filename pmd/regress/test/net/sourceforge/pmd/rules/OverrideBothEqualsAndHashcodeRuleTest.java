/*
 * User: tom
 * Date: Jul 3, 2002
 * Time: 9:40:47 PM
 */
package test.net.sourceforge.pmd.rules;

import net.sourceforge.pmd.reports.Report;
import net.sourceforge.pmd.rules.EmptyWhileStmtRule;
import net.sourceforge.pmd.rules.OverrideBothEqualsAndHashcodeRule;

public class OverrideBothEqualsAndHashcodeRuleTest extends RuleTst {
    public OverrideBothEqualsAndHashcodeRuleTest( String name ) {
	super( name );
    }

    public void test1() throws Throwable{
        Report report = process("OverrideBothEqualsAndHashcode1.java", new OverrideBothEqualsAndHashcodeRule());
        assertEquals(1, report.size());
    }
    public void test2() throws Throwable{
        Report report = process("OverrideBothEqualsAndHashcode2.java", new OverrideBothEqualsAndHashcodeRule());
        assertEquals(1, report.size());
    }
    public void test3() throws Throwable{
        Report report = process("OverrideBothEqualsAndHashcode3.java", new OverrideBothEqualsAndHashcodeRule());
        assertEquals(0, report.size());
    }
    public void test4() throws Throwable{
        Report report = process("OverrideBothEqualsAndHashcode4.java", new OverrideBothEqualsAndHashcodeRule());
        assertEquals(0, report.size());
    }
}
