/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.ant;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.apache.tools.ant.BuildFileRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 *
 * @author Romain Pelisse &lt;belaran@gmail.com&gt;
 *
 */
public class CPDTaskTest {

    @Rule
    public final BuildFileRule buildRule = new BuildFileRule();

    @Before
    public void setUp() {
        buildRule.configureProject("src/test/resources/net/sourceforge/pmd/ant/xml/cpdtasktest.xml");
    }

    @Test
    public void testBasic() {
        buildRule.executeTarget("testBasic");
        // FIXME: This clearly needs to be improved - but I don't like to write
        // test, so feel free to contribute :)
        assertTrue(new File("target/cpd.ant.tests").exists());
    }
}
