/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package test.net.sourceforge.pmd.ant;

import junit.framework.TestCase;
import net.sourceforge.pmd.ant.Formatter;
import net.sourceforge.pmd.ant.PMDTask;
import net.sourceforge.pmd.ant.RuleSet;

import org.apache.tools.ant.BuildException;

public class PMDTaskTest extends TestCase {

    public void testNoFormattersValidation() {
        PMDTask task = new PMDTask();
        try {
            task.execute();
            throw new RuntimeException("Should have thrown a BuildException - no Formatters");
        } catch (BuildException be) {
            // cool
        }
    }

    public void testFormatterWithNoToFileAttribute() {
        PMDTask task = new PMDTask();
        task.addFormatter(new Formatter());
        try {
            task.execute();
            throw new RuntimeException("Should have thrown a BuildException - a Formatter was missing a toFile attribute");
        } catch (BuildException be) {
            // cool
        }
    }

    public void testNoRuleSets() {
        PMDTask task = new PMDTask();
        task.setPrintToConsole(true);
        try {
            task.execute();
            throw new RuntimeException("Should have thrown a BuildException - no rulesets");
        } catch (BuildException be) {
            // cool
        }
    }

    public void testNestedRuleset() {
        PMDTask task = new PMDTask();
        RuleSet r = new RuleSet();
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
}

