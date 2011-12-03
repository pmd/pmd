/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.ant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import net.sourceforge.pmd.ant.Version;

import org.junit.Test;

public class VersionTest {

    @Test
    public void testHappyPath() {
	Version version = new Version();
	assertNull("default", version.getTerseName());
	version.addText("terseName");
	assertEquals("set terse name", "terseName", version.getTerseName());
    }

    public static junit.framework.Test suite() {
	return new junit.framework.JUnit4TestAdapter(VersionTest.class);
    }
}
