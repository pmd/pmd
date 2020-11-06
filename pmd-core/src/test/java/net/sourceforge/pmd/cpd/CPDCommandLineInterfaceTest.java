/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;
import org.junit.contrib.java.lang.system.SystemOutRule;
import org.junit.rules.TestRule;

public class CPDCommandLineInterfaceTest {
    @Rule
    public final TestRule restoreSystemProperties = new RestoreSystemProperties();
    @Rule
    public final SystemOutRule log = new SystemOutRule().enableLog();

    @Test
    public void testEmptyResultRendering() {
        System.setProperty(CPDCommandLineInterface.NO_EXIT_AFTER_RUN, "true");
        CPDCommandLineInterface.main(new String[] { "--minimum-tokens", "340", "--language", "java", "--files",
            "src/test/resources/net/sourceforge/pmd/cpd/files/", "--format", "xml", });
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\n" + "<pmd-cpd/>", log.getLog());
    }
}
