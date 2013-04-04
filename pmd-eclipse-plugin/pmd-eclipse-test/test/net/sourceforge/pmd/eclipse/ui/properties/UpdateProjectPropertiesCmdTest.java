package net.sourceforge.pmd.eclipse.ui.properties;

import java.util.Iterator;

import name.herlin.command.CommandException;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.eclipse.EclipseUtils;
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.eclipse.runtime.properties.IProjectProperties;
import net.sourceforge.pmd.eclipse.runtime.properties.IProjectPropertiesManager;
import net.sourceforge.pmd.eclipse.runtime.properties.PropertiesException;

import org.eclipse.core.resources.IProject;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

@Ignore("doesn't work - workspace is closed")
public class UpdateProjectPropertiesCmdTest {
  private IProject testProject;

  @Before
  public void setUp() throws Exception {
    // 1. Create a Java project
    this.testProject = EclipseUtils.createJavaProject("PMDTestProject");
    Assert.assertTrue("A test project cannot be created; the tests cannot be performed.",
        this.testProject != null && this.testProject.exists() && this.testProject.isAccessible());
  }

  @After
  public void tearDown() throws Exception {
    try {
      // 1. Delete the test project
      if (this.testProject != null) {
        if (this.testProject.exists() && this.testProject.isAccessible()) {
          this.testProject.delete(true, true, null);
          this.testProject = null;
        }
      }
    }
    catch (final Exception e) {
      System.out.println("Exception " + e.getClass().getName() + " when tearing down. Ignored.");
    }
  }

  /**
   * Bug: when a user deselect a project rule it is not saved
   */
  @Test
  public void testBug() throws CommandException, PropertiesException {
    final RuleSetFactory factory = new RuleSetFactory();

    // First ensure that the plugin initial ruleset is equal to the project
    // ruleset
    final IProjectPropertiesManager mgr = PMDPlugin.getDefault().getPropertiesManager();
    final IProjectProperties model = mgr.loadProjectProperties(this.testProject);

    RuleSet projectRuleSet = model.getProjectRuleSet();
    Assert.assertEquals("The project ruleset is not equal to the plugin ruleset", PMDPlugin.getDefault().getPreferencesManager()
        .getRuleSet().getRules(), projectRuleSet.getRules());

    // 2. remove the first rule (keep its name for assertion)
    final RuleSet newRuleSet = new RuleSet();
    newRuleSet.addRuleSet(projectRuleSet);
    final Iterator<Rule> i = newRuleSet.getRules().iterator();
    final Rule removedRule = i.next();
    i.remove();

    final UpdateProjectPropertiesCmd cmd = new UpdateProjectPropertiesCmd();
    cmd.setPmdEnabled(true);
    cmd.setProject(this.testProject);
    cmd.setProjectRuleSet(newRuleSet);
    cmd.setProjectWorkingSet(null);
    cmd.setRuleSetStoredInProject(false);
    cmd.execute();

    // 3. test the rule has correctly been removed
    projectRuleSet = model.getProjectRuleSet();
    Assert.assertNull("The rule has not been removed!", projectRuleSet.getRuleByName(removedRule.getName()));
  }

}
