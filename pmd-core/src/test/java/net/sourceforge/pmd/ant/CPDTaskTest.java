/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.ant;

import java.io.File;

import org.apache.tools.ant.BuildFileTest;
import org.junit.Test;

/**
 *
 * @author Romain Pelisse <belaran@gmail.com>
 *
 */
public class CPDTaskTest extends BuildFileTest {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        configureProject("src/test/resources/net/sourceforge/pmd/ant/xml/cpdtasktest.xml");
    }

    @Test
    public void testBasic() {
        executeTarget("testBasic");
        // FIXME: This clearly needs to be improved - but I don't like to write
        // test,
        // so feel free to contribute :)
        assertTrue(new File("target/cpd.ant.tests").exists());
    }
}
