/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.ant;

import net.sourceforge.pmd.ant.Formatter;
import net.sourceforge.pmd.ant.PMDTask;
import net.sourceforge.pmd.ant.RuleSetWrapper;

import org.apache.tools.ant.BuildException;
import org.junit.Ignore;
import org.junit.Test;

public class PMDTaskTest {

    @Test(expected = BuildException.class)
    public void testNoFormattersValidation() {
        PMDTask task = new PMDTask();
        task.execute();
    }

    @Test(expected = BuildException.class)
    public void testFormatterWithNoToFileAttribute() {
        PMDTask task = new PMDTask();
        task.addFormatter(new Formatter());
        task.execute();
    }

    @Test(expected = BuildException.class)
    public void testNoRuleSets() {
        PMDTask task = new PMDTask();
        task.execute();
    }

    @Ignore("This test has a TODO in it")
    @Test
    public void testNestedRuleset() {
        PMDTask task = new PMDTask();
        RuleSetWrapper r = new RuleSetWrapper();
        r.addText("rulesets/basic.xml");
        task.addRuleset(r);
        r.addText("rulesets/design.xml");
        task.addRuleset(r);
        Formatter f = new Formatter();
        task.addFormatter(f);
        
        //TODO
        try {
            task.execute();
        } catch (BuildException be) {
            //fail(be.toString());
        }
    }

    @Test(expected = BuildException.class)
    public void testInvalidJDK() {
        PMDTask task = new PMDTask();
        task.setTargetJDK("1.7");
        task.execute();
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(PMDTaskTest.class);
    }
}
