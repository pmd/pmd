/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.ant;

import static java.io.File.separator;

import java.io.File;
import java.io.IOException;

import org.apache.tools.ant.BuildFileRule;
import org.apache.tools.ant.Project;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.TemporaryFolder;


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
    public final TemporaryFolder tempFolder = new TemporaryFolder();

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
    public void setUp() throws IOException {
        validatePostConstruct();
        // initialize Ant
        buildRule.configureProject(pathToTestScript + separator + antTestScriptFilename);

        // Each test case gets one temp file name, accessible with ${tmpfile}
        final File newFile = tempFolder.newFile();
        newFile.delete(); // It shouldn't exist yet, but we want a unique name
        buildRule.getProject().setProperty("tmpfile", newFile.getAbsolutePath());

        Project project = buildRule.getProject();
        if (!project.getBaseDir().toString().endsWith(mvnWorkaround)) {
            // when running from maven, the path needs to be adapted...
            // FIXME: this is more a workaround than a good solution...
            project.setBasedir(project.getBaseDir().toString() + separator + pathToTestScript);
        }
    }


    /**
     * Returns the current temporary file. Replaced by a fresh (inexistent)
     * file before each test.
     */
    public File currentTempFile() {
        String tmpname = buildRule.getProject().getProperty("tmpfile");
        return tmpname == null ? null : new File(tmpname);
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
        assertContains(buildRule.getOutput(), text);
    }


    public void assertContains(String text, String toFind) {
        Assert.assertTrue("Expected to find \"" + toFind + "\", but it's missing",
                          text.contains(toFind));
    }


    public void assertDoesntContain(String text, String toFind) {
        Assert.assertTrue("Expected no occurrence of \"" + toFind + "\", but found at least one",
                          !text.contains(toFind));
    }
}
