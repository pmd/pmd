/*
 * Created on 6 fevr. 2005
 *
 * Copyright (c) 2004, PMD for Eclipse Development Team
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * The end-user documentation included with the redistribution, if
 *       any, must include the following acknowledgement:
 *       "This product includes software developed in part by support from
 *        the Defense Advanced Research Project Agency (DARPA)"
 *     * Neither the name of "PMD for Eclipse Development Team" nor the names of its
 *       contributors may be used to endorse or promote products derived from
 *       this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
 * PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package test.net.sourceforge.pmd.eclipse.model;

import java.util.Iterator;

import junit.framework.TestCase;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.eclipse.PMDPlugin;
import net.sourceforge.pmd.eclipse.PMDPluginConstants;
import net.sourceforge.pmd.eclipse.builder.PMDNature;
import net.sourceforge.pmd.eclipse.model.ModelException;
import net.sourceforge.pmd.eclipse.model.ModelFactory;
import net.sourceforge.pmd.eclipse.model.ProjectPropertiesModel;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IWorkingSet;

import test.net.sourceforge.pmd.eclipse.EclipseUtils;

/**
 * Test the project properties model.
 * 
 * @author Philippe Herlin
 * @version $Revision$
 * 
 * $Log$
 * Revision 1.1  2005/06/15 21:14:56  phherlin
 * Create the project for the Eclipse plugin unit tests
 *
 *  
 */
public class ProjectPropertiesModelTest extends TestCase {
    private IProject testProject;

    /**
     * Test case constructor
     * 
     * @param name
     *            of the test case
     */
    public ProjectPropertiesModelTest(String name) {
        super(name);
    }

    /**
     * A property should be used to know if PMD is enabled for a project. Set to
     * TRUE
     *  
     */
    public void testPmdEnabledTRUE() throws ModelException, CoreException {
        ProjectPropertiesModel model = ModelFactory.getFactory().getProperiesModelForProject(this.testProject);

        model.setPmdEnabled(true);
        model.sync();
        assertTrue("Cannot activate PMD for that project", this.testProject.hasNature(PMDNature.PMD_NATURE));
        assertTrue("PMD Property not set!", model.isPmdEnabled());
    }

    /**
     * A property should be used to know id PMD is enabled for a project. Set to
     * FALSE.
     *  
     */
    public void testPmdEnabledFALSE() throws ModelException, CoreException {
        ProjectPropertiesModel model = ModelFactory.getFactory().getProperiesModelForProject(this.testProject);

        model.setPmdEnabled(true);
        model.sync();
        assertTrue("Cannot activate PMD for that project", this.testProject.hasNature(PMDNature.PMD_NATURE));

        model.setPmdEnabled(false);
        model.sync();
        assertFalse("Cannot desactivate PMD for that project", this.testProject.hasNature(PMDNature.PMD_NATURE));
        assertFalse("PMD Property not reset!", model.isPmdEnabled());

    }

    /**
     * A brand new project should be affected the Plugin ruleset in the global
     * ruleset.
     *  
     */
    public void testProjectRuleSet() throws ModelException {
        ProjectPropertiesModel model = ModelFactory.getFactory().getProperiesModelForProject(this.testProject);
        assertSame("A new project a is not set the plugin ruleset", model.getProjectRuleSet(), PMDPlugin.getDefault().getRuleSet());
    }

    /**
     * Set another ruleset.
     */
    public void testProjectRuleSet1() throws ModelException, RuleSetNotFoundException, CoreException {
        ProjectPropertiesModel model = ModelFactory.getFactory().getProperiesModelForProject(this.testProject);
        RuleSetFactory factory = new RuleSetFactory();

        // use the basic ruleset because it should be included in the plugin
        // ruleset.
        RuleSet basicRuleSet = factory.createRuleSet("rulesets/basic.xml");

        // First set the project ruleset
        model.setProjectRuleSet(basicRuleSet);
        model.sync();
        assertNotNull("Project ruleset has not been set", model.getProjectRuleSet());

        // Then query the project ruleset
        RuleSet projectRuleSet = model.getProjectRuleSet();

        // Then query the project ruleset (from model)
        projectRuleSet = model.getProjectRuleSet();

        // Test the ruleset we set is equal to the ruleset we queried
        Iterator i = projectRuleSet.getRules().iterator();
        while (i.hasNext()) {
            Rule rule = (Rule) i.next();
            try {
                Rule pmdRule = basicRuleSet.getRuleByName(rule.getName());
            } catch (RuntimeException e) {
                fail("The project rule " + rule.getName() + " doesn't exist in the plugin configuration");
            }
        }

        i = basicRuleSet.getRules().iterator();
        while (i.hasNext()) {
            Rule pmdRule = (Rule) i.next();
            try {
                Rule rule = projectRuleSet.getRuleByName(pmdRule.getName());
            } catch (RuntimeException e) {
                fail("The plugin rule " + pmdRule.getName() + " doesn't exist in the project properties");
            }
        }

    }

    /**
     * It should not be possible to set to null a project ruleset
     *  
     */
    public void testProjectRuleSetNull() throws ModelException {
        ProjectPropertiesModel model = ModelFactory.getFactory().getProperiesModelForProject(this.testProject);
        try {
            model.setProjectRuleSet(null);
            fail("A ModelException must be raised when setting a project ruleset to null");
        } catch (ModelException e) {
            // OK that's correct
        }

    }

    /**
     * A project may have its ruleset stored in the project own directory. Test
     * set to TRUE.
     */
    public void testRuleSetStoredInProjectTRUE() throws ModelException, RuleSetNotFoundException {
        ProjectPropertiesModel model = ModelFactory.getFactory().getProperiesModelForProject(this.testProject);
        RuleSetFactory factory = new RuleSetFactory();
        RuleSet basicRuleSet = factory.createRuleSet("rulesets/basic.xml");
        model.setPmdEnabled(true);
        model.setRuleSetStoredInProject(false);
        model.setProjectWorkingSet(null);
        model.setProjectRuleSet(basicRuleSet);
        model.sync();
        
        model.createDefaultRuleSetFile();
        model.setRuleSetStoredInProject(true);
        model.sync();
        
        boolean b = model.isRuleSetStoredInProject();
        IFile file = this.testProject.getFile(PMDPluginConstants.PROJECT_RULESET_FILE);
        RuleSet projectRuleSet = factory.createRuleSet(file.getLocation().toOSString());
        assertTrue("the ruleset should be stored in the project", b);
        assertEquals("The project ruleset must be equals to the one found in the project", model.getProjectRuleSet(),
                projectRuleSet);
    }

    /**
     * A project may have its ruleset stored in the project own directory. Test
     * set to FALSE.
     *  
     */
    public void testRuleSetStoredInProjectFALSE() throws ModelException, RuleSetNotFoundException {
        ProjectPropertiesModel model = ModelFactory.getFactory().getProperiesModelForProject(this.testProject);
        RuleSetFactory factory = new RuleSetFactory();
        RuleSet basicRuleSet = factory.createRuleSet("rulesets/basic.xml");
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
        boolean b = model.isRuleSetStoredInProject();
        assertFalse("the ruleset should'nt be stored in the project", b);
        assertTrue("The project ruleset must now be the plugin ruleset", PMDPlugin.getDefault().getRuleSet().equals(
                model.getProjectRuleSet()));
    }

    /**
     * A project may work only on a subset of files defined by a working set
     *  
     */
    public void testProjectWorkingSetNull() throws ModelException {
        ProjectPropertiesModel model = ModelFactory.getFactory().getProperiesModelForProject(this.testProject);
        model.setProjectWorkingSet(null);
        IWorkingSet w = model.getProjectWorkingSet();
        assertNull("The project should not have a working set defined", w);
    }

    /**
     * A project may know if it should be rebuilt or not
     *  
     */
    public void testRebuild1() throws ModelException {
        ProjectPropertiesModel model = ModelFactory.getFactory().getProperiesModelForProject(this.testProject);
        model.setPmdEnabled(false);
        model.setProjectWorkingSet(null);
        model.setRuleSetStoredInProject(false);
        model.setNeedRebuild(false);
        assertFalse(model.isNeedRebuild());
    }

    /**
     * A project may know if it should be rebuilt or not
     *  
     */
    public void testRebuild2() throws ModelException {
        ProjectPropertiesModel model = ModelFactory.getFactory().getProperiesModelForProject(this.testProject);
        model.setPmdEnabled(true);
        assertTrue(model.isNeedRebuild());
    }

    /**
     * A project may know if it should be rebuilt or not
     *  
     */
    public void testRebuild3() throws ModelException {
        ProjectPropertiesModel model = ModelFactory.getFactory().getProperiesModelForProject(this.testProject);
        RuleSet pmdRuleSet = PMDPlugin.getDefault().getRuleSet();
        RuleSet fooRuleSet = new RuleSet();

        Rule rule1 = pmdRuleSet.getRuleByName("EmptyCatchBlock");

        fooRuleSet.addRule(rule1);

        model.setProjectRuleSet(fooRuleSet);
        assertTrue(model.isNeedRebuild());
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    protected void setUp() throws Exception {
        super.setUp();

        // 1. Create a Java project
        this.testProject = EclipseUtils.createJavaProject("PMDTestProject");
        assertTrue("A test project cannot be created; the tests cannot be performed.", (this.testProject != null)
                && this.testProject.exists() && this.testProject.isAccessible());
    }

    /**
     * @see junit.framework.TestCase#tearDown()
     */
    protected void tearDown() throws Exception {
        if (this.testProject != null) {
            if (this.testProject.exists() && this.testProject.isAccessible()) {
                this.testProject.delete(true, true, null);
                this.testProject = null;
            }
        }

        super.tearDown();
    }

}