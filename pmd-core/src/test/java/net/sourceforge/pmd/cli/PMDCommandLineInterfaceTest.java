/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli;

import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.cache.NoopAnalysisCache;


/**
 * Unit test for {@link PMDCommandLineInterface}
 */
public class PMDCommandLineInterfaceTest {
    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    @Rule // Restores system properties after test
    public final RestoreSystemProperties restoreSystemProperties = new RestoreSystemProperties();

    @Before
    public void clearSystemProperties() {
        System.clearProperty(PMDCommandLineInterface.NO_EXIT_AFTER_RUN);
        System.clearProperty(PMDCommandLineInterface.STATUS_CODE_PROPERTY);
    }

    @Test
    public void testProperties() {
        PMDParameters params = new PMDParameters();
        String[] args = { "-d", "source_folder", "-f", "yahtml", "-P", "outputDir=output_folder", "-R", "java-empty", };
        PMDCommandLineInterface.extractParameters(params, args, "PMD");

        Assert.assertEquals("output_folder", params.getProperties().getProperty("outputDir"));
    }

    @Test
    public void testMultipleProperties() {
        PMDParameters params = new PMDParameters();
        String[] args = { "-d", "source_folder", "-f", "ideaj", "-P", "sourcePath=/home/user/source/", "-P",
            "fileName=Foo.java", "-P", "classAndMethodName=Foo.method", "-R", "java-empty", };
        PMDCommandLineInterface.extractParameters(params, args, "PMD");

        Assert.assertEquals("/home/user/source/", params.getProperties().getProperty("sourcePath"));
        Assert.assertEquals("Foo.java", params.getProperties().getProperty("fileName"));
        Assert.assertEquals("Foo.method", params.getProperties().getProperty("classAndMethodName"));
    }


    @Test
    public void testNoCacheSwitch() {
        PMDParameters params = new PMDParameters();
        String[] args = {"-d", "source_folder", "-f", "ideaj", "-R", "java-empty", "-cache", "/home/user/.pmd/cache", "-no-cache", };
        PMDCommandLineInterface.extractParameters(params, args, "PMD");

        assertTrue(params.isIgnoreIncrementalAnalysis());
        PMDConfiguration config = params.toConfiguration();
        assertTrue(config.isIgnoreIncrementalAnalysis());
        assertTrue(config.getAnalysisCache() instanceof NoopAnalysisCache);
    }

    @Test
    public void testSetStatusCodeOrExitDoExit() {
        exit.expectSystemExitWithStatus(0);

        PMDCommandLineInterface.setStatusCodeOrExit(0);
    }

    @Test
    public void testSetStatusCodeOrExitSetStatus() {
        System.setProperty(PMDCommandLineInterface.NO_EXIT_AFTER_RUN, "1");

        PMDCommandLineInterface.setStatusCodeOrExit(0);
        Assert.assertEquals(System.getProperty(PMDCommandLineInterface.STATUS_CODE_PROPERTY), "0");
    }
}
