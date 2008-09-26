package net.sourceforge.pmd.ui.properties;

import java.util.Iterator;

import junit.framework.TestCase;
import name.herlin.command.CommandException;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.eclipse.EclipseUtils;
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.runtime.properties.IProjectProperties;
import net.sourceforge.pmd.runtime.properties.IProjectPropertiesManager;
import net.sourceforge.pmd.runtime.properties.PropertiesException;
import net.sourceforge.pmd.ui.properties.UpdateProjectPropertiesCmd;

import org.eclipse.core.resources.IProject;


public class UpdateProjectPropertiesCmdTest extends TestCase {
    private IProject testProject;

    protected void setUp() throws Exception {
        super.setUp();

        // 1. Create a Java project
        this.testProject = EclipseUtils.createJavaProject("PMDTestProject");
        assertTrue("A test project cannot be created; the tests cannot be performed.", (this.testProject != null)
                && this.testProject.exists() && this.testProject.isAccessible());
    }

    protected void tearDown() throws Exception {
        try {
            // 1. Delete the test project
            if (this.testProject != null) {
                if (this.testProject.exists() && this.testProject.isAccessible()) {
                    this.testProject.delete(true, true, null);
                    this.testProject = null;
                }
            }

            super.tearDown();

        } catch (Exception e) {
            System.out.println("Exception " + e.getClass().getName() + " when tearing down. Ignored.");
        }
    }

    /**
     * Bug: when a user deselect a project rule it is not saved
     */
    public void testBug() throws CommandException, PropertiesException {
        RuleSetFactory factory = new RuleSetFactory();

        // First ensure that the plugin initial ruleset is equal to the project
        // ruleset
        IProjectPropertiesManager mgr = PMDPlugin.getDefault().getPropertiesManager();
        IProjectProperties model = mgr.loadProjectProperties(this.testProject);

        RuleSet projectRuleSet = model.getProjectRuleSet();
        assertEquals("The project ruleset is not equal to the plugin ruleset", PMDPlugin.getDefault()
                .getPreferencesManager().getRuleSet().getRules(), projectRuleSet.getRules());

        // 2. remove the first rule (keep its name for assertion)
        RuleSet newRuleSet = new RuleSet();
        newRuleSet.addRuleSet(projectRuleSet);
        Iterator i = newRuleSet.getRules().iterator();
        Rule removedRule = (Rule) i.next();
        i.remove();

        UpdateProjectPropertiesCmd cmd = new UpdateProjectPropertiesCmd();
        cmd.setPmdEnabled(true);
        cmd.setProject(this.testProject);
        cmd.setProjectRuleSet(newRuleSet);
        cmd.setProjectWorkingSet(null);
        cmd.setRuleSetStoredInProject(false);
        cmd.execute();

        // 3. test the rule has correctly been removed
        projectRuleSet = model.getProjectRuleSet();
        assertNull("The rule has not been removed!", projectRuleSet.getRuleByName(removedRule.getName()));
    }

}
