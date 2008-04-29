import net.sourceforge.pmd.ant.PmdBuildTask;

import org.apache.tools.ant.BuildException;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author rpelisse
 *
 */
public class AntTaskTest {

    /**
     * @throws java.lang.Exception
     */
    @BeforeClass
    public static void setUpBeforeClass() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void antTask() {
	PmdBuildTask task = new PmdBuildTask();
	task.setRulesDirectory("rulesets");
	task.setTarget("target-test");
	try {
	    task.execute();
	} catch (BuildException e) {
	    e.printStackTrace();
	}

    }
}
