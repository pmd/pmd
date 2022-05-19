/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.ant;

import java.io.File;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Romain Pelisse &lt;belaran@gmail.com&gt;
 *
 */
public class CPDTaskTest extends AbstractAntTest {

    @BeforeEach
    public void setUp() {
        configureProject("src/test/resources/net/sourceforge/pmd/ant/xml/cpdtasktest.xml");
    }

    @Test
    public void testBasic() {
        executeTarget("testBasic");
        // FIXME: This clearly needs to be improved - but I don't like to write
        // test, so feel free to contribute :)
        Assertions.assertTrue(new File("target/cpd.ant.tests").exists());
    }
}
