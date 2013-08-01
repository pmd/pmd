package net.sourceforge.pmd.ant;

import static java.io.File.separator;

import java.io.File;

import org.apache.tools.ant.BuildFileTest;

/**
 * Quite an ugly classe, arguably useful for just 2 units test - nevertheless as
 * there is a workaround that must be shared by both tests (PMD and CPD's) I felt
 * compelled to move it to a single classes.
 *
 * @author Romain Pelisse <belaran@gmail.com>
 *
 */
public abstract class AbstractAntTestHelper extends BuildFileTest {

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

    @Override
    public void setUp() {
    	validatePostConstruct();
        // initialize Ant
        configureProject(pathToTestScript + separator + antTestScriptFilename);
        if (!project.getBaseDir().toString().endsWith(mvnWorkaround)) {
            // when running from maven, the path needs to be adapted...
            // FIXME: this is more a workaround than a good solution...
            project.setBasedir(project.getBaseDir().toString()
        	    + separator + pathToTestScript);
        }
    }

	private void validatePostConstruct() {
		if ( pathToTestScript == null || "".equals(pathToTestScript) ||
			 antTestScriptFilename == null || "".equals(antTestScriptFilename) ||
			 mvnWorkaround == null || "".equals(mvnWorkaround) )
			throw new IllegalStateException("Unit tests for Ant script badly initialized");

	}
}
