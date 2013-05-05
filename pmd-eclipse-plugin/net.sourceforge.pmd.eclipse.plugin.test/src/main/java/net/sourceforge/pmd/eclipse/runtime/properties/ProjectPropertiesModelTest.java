/*
 * Created on 6 fevr. 2005
 * 
 * Copyright (c) 2004, PMD for Eclipse Development Team All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 * * Redistributions of source code must retain the above copyright notice,
 * this list of conditions and the following disclaimer. * Redistributions
 * in binary form must reproduce the above copyright notice, this list of
 * conditions and the following disclaimer in the documentation and/or other
 * materials provided with the distribution. * The end-user documentation
 * included with the redistribution, if any, must include the following
 * acknowledgement: "This product includes software developed in part by
 * support from the Defense Advanced Research Project Agency (DARPA)" *
 * Neither the name of "PMD for Eclipse Development Team" nor the names of
 * its contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.sourceforge.pmd.eclipse.runtime.properties;

import java.util.Iterator;
import java.util.Set;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.eclipse.EclipseUtils;
import net.sourceforge.pmd.eclipse.plugin.PMDPlugin;
import net.sourceforge.pmd.eclipse.runtime.builder.PMDNature;
import net.sourceforge.pmd.eclipse.runtime.preferences.IPreferencesManager;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IWorkingSet;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test the project properties model.
 * 
 * @author Philippe Herlin
 * 
 */
@Ignore("doesn't work - workspace is closed")
public class ProjectPropertiesModelTest {
  private IProject testProject;
  private RuleSet initialPluginRuleSet;

  /**
   * @see junit.framework.TestCase#setUp()
   */
  @Before
  public void setUp() throws Exception {

    // 1. Create a Java project
    this.testProject = EclipseUtils.createJavaProject("PMDTestProject");
    Assert.assertTrue("A test project cannot be created; the tests cannot be performed.",
        this.testProject != null && this.testProject.exists() && this.testProject.isAccessible());

    // 2. Keep the plugin ruleset
    this.initialPluginRuleSet = PMDPlugin.getDefault().getPreferencesManager().getRuleSet();
    this.initialPluginRuleSet.getRules().clear();
    final Set<RuleSet> defaultRuleSets = PMDPlugin.getDefault().getRuleSetManager().getDefaultRuleSets();
    for (final RuleSet ruleSet : defaultRuleSets) {
      this.initialPluginRuleSet.addRuleSet(ruleSet);
    }
  }

  /**
   * @see junit.framework.TestCase#tearDown()
   */
  @After
  public void tearDown() throws Exception {
    // 1. Delete the test project
    if (this.testProject != null) {
      if (this.testProject.exists() && this.testProject.isAccessible()) {
        this.testProject.delete(true, true, null);
        this.testProject = null;
      }
    }

    // 2. Restore the plugin initial rule set
    PMDPlugin.getDefault().getPreferencesManager().setRuleSet(this.initialPluginRuleSet);
  }

  /**
   * Bug: when a user deselect a project rule it is not saved
   */
  @Test
  public void testBug() throws PropertiesException, RuleSetNotFoundException, CoreException {
    final RuleSetFactory factory = new RuleSetFactory();

    // First ensure that the plugin initial ruleset is equal to the project
    // ruleset
    final IProjectPropertiesManager mgr = PMDPlugin.getDefault().getPropertiesManager();
    final IProjectProperties model = mgr.loadProjectProperties(this.testProject);

    RuleSet projectRuleSet = model.getProjectRuleSet();
    Assert.assertEquals("The project ruleset is not equal to the plugin ruleset", this.initialPluginRuleSet.getRules(),
        projectRuleSet.getRules());

    // 2. remove the first rule (keep its name for assertion)
    final RuleSet newRuleSet = new RuleSet();
    newRuleSet.addRuleSet(projectRuleSet);
    final Iterator<Rule> i = newRuleSet.getRules().iterator();
    final Rule removedRule = i.next();
    i.remove();

    model.setProjectRuleSet(newRuleSet);
    model.sync();

    // 3. test the rule has correctly been removed
    projectRuleSet = model.getProjectRuleSet();
    Assert.assertNull("The rule has not been removed!", projectRuleSet.getRuleByName(removedRule.getName()));
  }

  /**
   * A property should be used to know id PMD is enabled for a project. Set
   * to FALSE.
   * 
   */
  @Test
  public void testPmdEnabledFALSE() throws PropertiesException, CoreException {
    final IProjectPropertiesManager mgr = PMDPlugin.getDefault().getPropertiesManager();
    final IProjectProperties model = mgr.loadProjectProperties(this.testProject);

    model.setPmdEnabled(true);
    model.sync();
    Assert.assertTrue("Cannot activate PMD for that project", this.testProject.hasNature(PMDNature.PMD_NATURE));

    model.setPmdEnabled(false);
    model.sync();
    Assert.assertFalse("Cannot desactivate PMD for that project", this.testProject.hasNature(PMDNature.PMD_NATURE));
    Assert.assertFalse("PMD Property not reset!", model.isPmdEnabled());

  }

  /**
   * A property should be used to know if PMD is enabled for a project. Set
   * to TRUE
   * 
   */
  @Test
  public void testPmdEnabledTRUE() throws CoreException, PropertiesException {
    final IProjectPropertiesManager mgr = PMDPlugin.getDefault().getPropertiesManager();
    final IProjectProperties model = mgr.loadProjectProperties(this.testProject);

    model.setPmdEnabled(true);
    model.sync();
    Assert.assertTrue("Cannot activate PMD for that project", this.testProject.hasNature(PMDNature.PMD_NATURE));
    Assert.assertTrue("PMD Property not set!", model.isPmdEnabled());
  }

  /**
   * A brand new project should be affected the Plugin ruleset in the global
   * ruleset.
   * 
   */
  @Test
  public void testProjectRuleSet() throws PropertiesException {
    final IProjectPropertiesManager mgr = PMDPlugin.getDefault().getPropertiesManager();
    final IProjectProperties model = mgr.loadProjectProperties(this.testProject);

    final IPreferencesManager pmgr = PMDPlugin.getDefault().getPreferencesManager();

    Assert.assertSame("A new project is not set the plugin ruleset", model.getProjectRuleSet(), pmgr.getRuleSet());
  }

  /**
   * Set another ruleset.
   */
  @Test
  public void testProjectRuleSet1() throws PropertiesException, RuleSetNotFoundException, CoreException {
    final IProjectPropertiesManager mgr = PMDPlugin.getDefault().getPropertiesManager();
    final IProjectProperties model = mgr.loadProjectProperties(this.testProject);

    final RuleSetFactory factory = new RuleSetFactory();

    // use the basic ruleset because it should be included in the plugin
    // ruleset.
    final RuleSet basicRuleSet = factory.createRuleSet("rulesets/java/basic.xml");

    // First set the project ruleset
    model.setProjectRuleSet(basicRuleSet);
    model.sync();

    // Test the ruleset we set is equal to the ruleset we queried
    final RuleSet projectRuleSet = model.getProjectRuleSet();
    Assert.assertNotNull("Project ruleset has not been set", projectRuleSet);
    Assert.assertTrue("The project ruleset is not the basic ruleset",
        EclipseUtils.assertRuleSetEquals(basicRuleSet.getRules(), projectRuleSet.getRules(), System.out));
  }

  /**
   * When rules are removed from the plugin preferences, these rules should
   * also be removed from the project euh... ben en fait non. annulé.
   */
  @Test
  public void testProjectRuleSet2() throws PropertiesException, RuleSetNotFoundException, CoreException {
    /*
            // First ensure that the plugin initial ruleset is equal to the project
            // ruleset
            IProjectPropertiesManager mgr = PMDPlugin.getDefault().getPropertiesManager();
            IProjectProperties model = mgr.loadProjectProperties(this.testProject);

            RuleSet projectRuleSet = model.getProjectRuleSet();
            assertEquals("The project ruleset is not equal to the plugin ruleset", this.initialPluginRuleSet.getRules(), projectRuleSet
                    .getRules());

            // use the basic ruleset and set it at the only plugin ruleset
            RuleSetFactory factory = new RuleSetFactory();
            RuleSet basicRuleSet = factory.createSingleRuleSet("rulesets/basic.xml");

            IPreferencesManager pmgr = PMDPlugin.getDefault().getPreferencesManager();
            pmgr.setRuleSet(basicRuleSet);

            projectRuleSet = model.getProjectRuleSet();

            dumpRuleSet(basicRuleSet);
            dumpRuleSet(projectRuleSet);
            assertEquals("The project ruleset is not equal to the plugin ruleset", basicRuleSet.getRules(), projectRuleSet.getRules());
    */
  }

  /**
   * When rules are added to the plugin preferences, these rules should also
   * be added to the project
   */
  @Test
  public void testProjectRuleSet3() throws PropertiesException, RuleSetNotFoundException, CoreException {

    // First ensure that the plugin initial ruleset is equal to the project
    // ruleset
    final IProjectPropertiesManager mgr = PMDPlugin.getDefault().getPropertiesManager();
    IProjectProperties model = mgr.loadProjectProperties(this.testProject);

    RuleSet projectRuleSet = model.getProjectRuleSet();
    Assert.assertEquals("The project ruleset is not equal to the plugin ruleset", this.initialPluginRuleSet.getRules(),
        projectRuleSet.getRules());

    // 2. add a rule to the plugin rule set
    final Rule myRule = new AbstractJavaRule() {
      @Override
      public String getName() {
        return "MyRule";
      }
    };

    final RuleSet newRuleSet = new RuleSet();
    newRuleSet.setName("foo");
    newRuleSet.addRuleSet(this.initialPluginRuleSet);
    newRuleSet.addRule(myRule);
    PMDPlugin.getDefault().getPreferencesManager().setRuleSet(newRuleSet);

    // Test that the project rule set should still be the same as the plugin
    // rule set
    model = mgr.loadProjectProperties(this.testProject);
    projectRuleSet = model.getProjectRuleSet();
    Assert.assertEquals("The project ruleset is not equal to the plugin ruleset", PMDPlugin.getDefault().getPreferencesManager()
        .getRuleSet().getRules(), projectRuleSet.getRules());
  }

  /**
   * It should not be possible to set to null a project ruleset
   * 
   */
  @Test
  public void testProjectRuleSetNull() throws PropertiesException {
    final IProjectPropertiesManager mgr = PMDPlugin.getDefault().getPropertiesManager();
    final IProjectProperties model = mgr.loadProjectProperties(this.testProject);

    try {
      model.setProjectRuleSet(null);
      Assert.fail("A ModelException must be raised when setting a project ruleset to null");
    }
    catch (final PropertiesException e) {
      // OK that's correct
    }

  }

  /**
   * A project may work only on a subset of files defined by a working set
   * 
   */
  @Test
  public void testProjectWorkingSetNull() throws PropertiesException {
    final IProjectPropertiesManager mgr = PMDPlugin.getDefault().getPropertiesManager();
    final IProjectProperties model = mgr.loadProjectProperties(this.testProject);

    model.setProjectWorkingSet(null);
    final IWorkingSet w = model.getProjectWorkingSet();
    Assert.assertNull("The project should not have a working set defined", w);
  }

  /**
   * A project may know if it should be rebuilt or not
   * 
   */
  @Test
  public void testRebuild1() throws PropertiesException {
    final IProjectPropertiesManager mgr = PMDPlugin.getDefault().getPropertiesManager();
    final IProjectProperties model = mgr.loadProjectProperties(this.testProject);

    model.setPmdEnabled(false);
    model.setProjectWorkingSet(null);
    model.setRuleSetStoredInProject(false);
    model.setNeedRebuild(false);
    Assert.assertFalse(model.isNeedRebuild());
  }

  /**
   * A project may know if it should be rebuilt or not
   * 
   */
  @Test
  public void testRebuild2() throws PropertiesException {
    final IProjectPropertiesManager mgr = PMDPlugin.getDefault().getPropertiesManager();
    final IProjectProperties model = mgr.loadProjectProperties(this.testProject);

    model.setPmdEnabled(true);
    Assert.assertTrue(model.isNeedRebuild());
  }

  /**
   * A project may know if it should be rebuilt or not
   * 
   */
  @Test
  public void testRebuild3() throws PropertiesException {
    final IProjectPropertiesManager mgr = PMDPlugin.getDefault().getPropertiesManager();
    final IProjectProperties model = mgr.loadProjectProperties(this.testProject);

    model.setPmdEnabled(true);

    final RuleSet pmdRuleSet = PMDPlugin.getDefault().getPreferencesManager().getRuleSet();
    final RuleSet fooRuleSet = new RuleSet();

    final Rule rule1 = pmdRuleSet.getRuleByName("EmptyCatchBlock");

    fooRuleSet.addRule(rule1);

    model.setProjectRuleSet(fooRuleSet);
    Assert.assertTrue(model.isNeedRebuild());
  }

  /**
   * A project may have its ruleset stored in the project own directory.
   * Test set to FALSE.
   * 
   */
  @Test
  public void testRuleSetStoredInProjectFALSE() throws PropertiesException, RuleSetNotFoundException {
    final IProjectPropertiesManager mgr = PMDPlugin.getDefault().getPropertiesManager();
    final IProjectProperties model = mgr.loadProjectProperties(this.testProject);

    final RuleSetFactory factory = new RuleSetFactory();
    final RuleSet basicRuleSet = factory.createRuleSet("rulesets/java/basic.xml");
    model.setPmdEnabled(true);
    model.setRuleSetStoredInProject(false);
    model.setProjectWorkingSet(null);
    model.setProjectRuleSet(basicRuleSet);
    model.sync();

    model.createDefaultRuleSetFile();
    model.setRuleSetStoredInProject(true);
    model.sync();

    model.setRuleSetStoredInProject(false);
    model.sync();
    final boolean b = model.isRuleSetStoredInProject();
    Assert.assertFalse("the ruleset should'nt be stored in the project", b);
  }

  /**
   * A project may have its ruleset stored in the project own directory.
   * Test set to TRUE.
   */
  @Test
  public void testRuleSetStoredInProjectTRUE() throws PropertiesException, RuleSetNotFoundException {
    final IProjectPropertiesManager mgr = PMDPlugin.getDefault().getPropertiesManager();
    final IProjectProperties model = mgr.loadProjectProperties(this.testProject);

    final RuleSetFactory factory = new RuleSetFactory();
    final RuleSet basicRuleSet = factory.createRuleSet("rulesets/java/basic.xml");
    model.setPmdEnabled(true);
    model.setRuleSetStoredInProject(false);
    model.setProjectWorkingSet(null);
    model.setProjectRuleSet(basicRuleSet);
    model.sync();

    model.createDefaultRuleSetFile();
    model.setRuleSetStoredInProject(true);
    model.sync();

    final boolean b = model.isRuleSetStoredInProject();
    final IFile file = this.testProject.getFile(".ruleset");
    final RuleSet projectRuleSet = factory.createRuleSet(file.getLocation().toOSString());
    Assert.assertTrue("the ruleset should be stored in the project", b);
    Assert.assertEquals("The project ruleset must be equals to the one found in the project", model.getProjectRuleSet(), projectRuleSet);
  }

  private void dumpRuleSet(final RuleSet ruleSet) {
    System.out.println("Dumping rule set:" + ruleSet.getName());
    for (final Rule rule : ruleSet.getRules()) {
      System.out.println(rule.getName());
    }
    System.out.println();
  }

}