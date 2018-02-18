/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.LogMode;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;
import org.junit.contrib.java.lang.system.StandardOutputStreamLog;
import org.junit.rules.TestRule;

import net.sourceforge.pmd.PMD;

public class CPDCommandLineInterfaceTest {
    @Rule
    public final TestRule restoreSystemProperties = new RestoreSystemProperties();
    @Rule
    public final StandardOutputStreamLog log = new StandardOutputStreamLog(LogMode.LOG_ONLY);

    @Test
    public void testEmptyResultRendering() {
        System.setProperty(CPDCommandLineInterface.NO_EXIT_AFTER_RUN, "true");
        CPDCommandLineInterface.main(new String[] { "--minimum-tokens", "340", "--language", "java", "--files",
            "src/test/resources/net/sourceforge/pmd/cpd/files/", "--format", "xml", });
        Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\n" + "<pmd-cpd/>", log.getLog());
    }
}
