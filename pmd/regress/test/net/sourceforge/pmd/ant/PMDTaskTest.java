/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package test.net.sourceforge.pmd.ant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import net.sourceforge.pmd.ant.Formatter;
import net.sourceforge.pmd.ant.PMDTask;
import net.sourceforge.pmd.ant.RuleSetWrapper;

import org.apache.tools.ant.BuildException;
import org.junit.Ignore;
import org.junit.Test;

public class PMDTaskTest {

    @Ignore("This test has a FIXME in it")
    @Test
    public void testNoFormattersValidation() {
	try {
            PMDTask task = new PMDTask();
            task.setRuleSetFiles("rulesets/design.xml");
            task.execute();
            // FIXME: no formatter is needed for ant to run
            // see TODO in PMDTask.validate()

            fail("Expecting BuildException exception");
	} catch (BuildException e) {
	    assertEquals("Valid Error Message", e.getMessage(), "<??>");
	}
    }

    @Test
    public void testFormatterWithNoToFileAttribute() {
	try {
            PMDTask task = new PMDTask();
            task.setRuleSetFiles("rulesets/design.xml");
            task.addFormatter(new Formatter());
            task.execute();
            fail("Expecting BuildException exception");
	} catch (BuildException e) {
	    assertEquals("Valid Error Message", e.getMessage(), "toFile or toConsole needs to be specified in Formatter");
	}
    }

    @Test
    public void testNoRuleSets() {
	try {
            PMDTask task = new PMDTask();
            task.execute();
            fail("Expecting BuildException exception");
	} catch (BuildException e) {
	    assertEquals("Valid Error Message", e.getMessage(), "No rulesets specified");
	}
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

    @Test
    public void testInvalidJDK() {
	try {
            PMDTask task = new PMDTask();
            task.setTargetJDK("42");
            task.setRuleSetFiles("rulesets/design.xml");
            task.execute();
            fail("Expecting BuildException exception");
	} catch (BuildException e) {
	    assertTrue("Valid Error Message", e.getMessage().startsWith("The targetjdk attribute, if used, must be set to either "));
	}
    }

    public static junit.framework.Test suite() {
        return new junit.framework.JUnit4TestAdapter(PMDTaskTest.class);
    }
}
