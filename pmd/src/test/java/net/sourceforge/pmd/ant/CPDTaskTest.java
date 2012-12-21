/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.ant;

import java.io.File;

import org.junit.Test;

/**
 *
 * @author Romain Pelisse <belaran@gmail.com>
 *
 */
public class CPDTaskTest extends AbstractAntTestHelper {

	public CPDTaskTest() {
		super.antTestScriptFilename = "cpdtasktest.xml";
	}

    @Test
    public void testBasic() {
        executeTarget("testBasic");
        // FIXME: This clearly needs to be improved - but I don't like to write test,
        //        so feel free to contribute :)
        assertTrue(new File("target/cpd.ant.tests").exists());
    }

}
