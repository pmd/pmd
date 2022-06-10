/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.cache.NoopAnalysisCache;

import com.github.stefanbirkner.systemlambda.SystemLambda;


/**
 * Unit test for {@link PMDCommandLineInterface}
 */
class PMDCommandLineInterfaceTest {

    @BeforeEach
    void clearSystemProperties() {
        System.clearProperty(PMDCommandLineInterface.NO_EXIT_AFTER_RUN);
        System.clearProperty(PMDCommandLineInterface.STATUS_CODE_PROPERTY);
    }

    @Test
    void testProperties() {
        PMDParameters params = new PMDParameters();
        String[] args = { "-d", "source_folder", "-f", "yahtml", "-P", "outputDir=output_folder", "-R", "java-empty", };
        PMDCommandLineInterface.extractParameters(params, args, "PMD");

        Assertions.assertEquals("output_folder", params.getProperties().getProperty("outputDir"));
    }

    @Test
    void testMultipleProperties() {
        PMDParameters params = new PMDParameters();
        String[] args = { "-d", "source_folder", "-f", "ideaj", "-P", "sourcePath=/home/user/source/", "-P",
            "fileName=Foo.java", "-P", "classAndMethodName=Foo.method", "-R", "java-empty", };
        PMDCommandLineInterface.extractParameters(params, args, "PMD");

        Assertions.assertEquals("/home/user/source/", params.getProperties().getProperty("sourcePath"));
        Assertions.assertEquals("Foo.java", params.getProperties().getProperty("fileName"));
        Assertions.assertEquals("Foo.method", params.getProperties().getProperty("classAndMethodName"));
    }


    @Test
    void testNoCacheSwitch() {
        PMDParameters params = new PMDParameters();
        String[] args = {"-d", "source_folder", "-f", "ideaj", "-R", "java-empty", "-cache", "/home/user/.pmd/cache", "-no-cache", };
        PMDCommandLineInterface.extractParameters(params, args, "PMD");

        Assertions.assertTrue(params.isIgnoreIncrementalAnalysis());
        PMDConfiguration config = params.toConfiguration();
        Assertions.assertTrue(config.isIgnoreIncrementalAnalysis());
        Assertions.assertTrue(config.getAnalysisCache() instanceof NoopAnalysisCache);
    }

    @Test
    void testNoCacheSwitchLongOption() {
        PMDParameters params = new PMDParameters();
        String[] args = {"-d", "source_folder", "-f", "ideaj", "-R", "java-empty", "--cache", "/home/user/.pmd/cache", "--no-cache", };
        PMDCommandLineInterface.extractParameters(params, args, "PMD");

        Assertions.assertTrue(params.isIgnoreIncrementalAnalysis());
        PMDConfiguration config = params.toConfiguration();
        Assertions.assertTrue(config.isIgnoreIncrementalAnalysis());
        Assertions.assertTrue(config.getAnalysisCache() instanceof NoopAnalysisCache);
    }

    @Test
    void testSetStatusCodeOrExitDoExit() throws Exception {
        int code = SystemLambda.catchSystemExit(() -> PMDCommandLineInterface.setStatusCodeOrExit(0));
        Assertions.assertEquals(0, code);
    }

    @Test
    void testSetStatusCodeOrExitSetStatus() {
        System.setProperty(PMDCommandLineInterface.NO_EXIT_AFTER_RUN, "1");

        PMDCommandLineInterface.setStatusCodeOrExit(0);
        Assertions.assertEquals(System.getProperty(PMDCommandLineInterface.STATUS_CODE_PROPERTY), "0");
    }

    @Test
    void testBuildUsageText() {
        // no exception..
        Assertions.assertNotNull(PMDCommandLineInterface.buildUsageText());
    }

}
