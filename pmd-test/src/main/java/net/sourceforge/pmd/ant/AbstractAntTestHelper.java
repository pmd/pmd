/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.ant;

import static java.io.File.separator;

import java.io.File;

import org.apache.tools.ant.BuildFileRule;
import org.apache.tools.ant.Project;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;

/**
 * Quite an ugly classe, arguably useful for just 2 units test - nevertheless as
 * there is a workaround that must be shared by both tests (PMD and CPD's) I
 * felt compelled to move it to a single classes.
 *
 * @author Romain Pelisse &lt;belaran@gmail.com&gt;
 *
 */
public abstract class AbstractAntTestHelper {

    @Rule
    public final BuildFileRule buildRule = new BuildFileRule();

    protected String pathToTestScript;
    protected String antTestScriptFilename;
    public String mvnWorkaround;

    public AbstractAntTestHelper() {
        mvnWorkaround = "pmd/ant/xml";
        if (new File("target/clover/test-classes").exists()) {
            pathToTestScript = "target/clover/test-classes/net/sourceforge/" + mvnWorkaround;
        } else {
            pathToTestScript = "target/test-classes/net/sourceforge/" + mvnWorkaround;
        }
    }

    @Before
    public void setUp() {
        validatePostConstruct();
        // initialize Ant
        buildRule.configureProject(pathToTestScript + separator + antTestScriptFilename);

        Project project = buildRule.getProject();
        if (!project.getBaseDir().toString().endsWith(mvnWorkaround)) {
            // when running from maven, the path needs to be adapted...
            // FIXME: this is more a workaround than a good solution...
            project.setBasedir(project.getBaseDir().toString() + separator + pathToTestScript);
        }
    }

    private void validatePostConstruct() {
        if (pathToTestScript == null || "".equals(pathToTestScript) || antTestScriptFilename == null
                || "".equals(antTestScriptFilename) || mvnWorkaround == null || "".equals(mvnWorkaround)) {
            throw new IllegalStateException("Unit tests for Ant script badly initialized");
        }
    }

    public void executeTarget(String target) {
        buildRule.executeTarget(target);
    }

    public void assertOutputContaining(String text) {
        Assert.assertTrue("Expected to find \"" + text + "\" in the output, but it's missing",
                buildRule.getOutput().contains(text));
    }
}
