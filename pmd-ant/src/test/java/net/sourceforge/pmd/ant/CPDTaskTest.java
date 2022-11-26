/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.ant;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 *
 * @author Romain Pelisse &lt;belaran@gmail.com&gt;
 *
 */
class CPDTaskTest extends AbstractAntTest {

    @BeforeEach
    void setUp() {
        configureProject("src/test/resources/net/sourceforge/pmd/ant/xml/cpdtasktest.xml");
    }

    @Test
    void testBasic() {
        executeTarget("testBasic");
        // FIXME: This clearly needs to be improved - but I don't like to write
        // test, so feel free to contribute :)
        assertTrue(new File("target/cpd.ant.tests").exists());
    }
}
