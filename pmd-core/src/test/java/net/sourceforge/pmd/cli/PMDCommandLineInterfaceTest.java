/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.cache.NoopAnalysisCache;
import net.sourceforge.pmd.lang.LanguageRegistry;

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

        assertEquals("output_folder", params.getProperties().getProperty("outputDir"));
    }

    @Test
    void testMultipleProperties() {
        PMDParameters params = new PMDParameters();
        String[] args = { "-d", "source_folder", "-f", "ideaj", "-P", "sourcePath=/home/user/source/", "-P",
            "fileName=Foo.java", "-P", "classAndMethodName=Foo.method", "-R", "java-empty", };
        PMDCommandLineInterface.extractParameters(params, args, "PMD");

        assertEquals("/home/user/source/", params.getProperties().getProperty("sourcePath"));
        assertEquals("Foo.java", params.getProperties().getProperty("fileName"));
        assertEquals("Foo.method", params.getProperties().getProperty("classAndMethodName"));
    }


    @Test
    void testNoCacheSwitch() {
        PMDParameters params = new PMDParameters();
        String[] args = {"-d", "source_folder", "-f", "ideaj", "-R", "java-empty", "-cache", "/home/user/.pmd/cache", "-no-cache", };
        PMDCommandLineInterface.extractParameters(params, args, "PMD");

        assertTrue(params.isIgnoreIncrementalAnalysis());
        PMDConfiguration config = params.toConfiguration(LanguageRegistry.PMD);
        assertTrue(config.isIgnoreIncrementalAnalysis());
        assertTrue(config.getAnalysisCache() instanceof NoopAnalysisCache);
    }

    @Test
    void testNoCacheSwitchLongOption() {
        PMDParameters params = new PMDParameters();
        String[] args = {"-d", "source_folder", "-f", "ideaj", "-R", "java-empty", "--cache", "/home/user/.pmd/cache", "--no-cache", };
        PMDCommandLineInterface.extractParameters(params, args, "PMD");

        assertTrue(params.isIgnoreIncrementalAnalysis());
        PMDConfiguration config = params.toConfiguration();
        assertTrue(config.isIgnoreIncrementalAnalysis());
        assertTrue(config.getAnalysisCache() instanceof NoopAnalysisCache);
    }

    @Test
    void testSetStatusCodeOrExitDoExit() throws Exception {
        int code = SystemLambda.catchSystemExit(() -> PMDCommandLineInterface.setStatusCodeOrExit(0));
        assertEquals(0, code);
    }

    @Test
    void testSetStatusCodeOrExitSetStatus() {
        System.setProperty(PMDCommandLineInterface.NO_EXIT_AFTER_RUN, "1");

        PMDCommandLineInterface.setStatusCodeOrExit(0);
        assertEquals(System.getProperty(PMDCommandLineInterface.STATUS_CODE_PROPERTY), "0");
    }

    @Test
    void testBuildUsageText() {
        // no exception..
        assertNotNull(PMDCommandLineInterface.buildUsageText());
    }

    @Test
    void testOnlyFileListOption() {
        PMDParameters params = new PMDParameters();
        String[] args = {"--file-list", "pmd.filelist", "-f", "text", "-R", "rulesets/java/quickstart.xml", "--no-cache", };
        PMDCommandLineInterface.extractParameters(params, args, "PMD");

        PMDConfiguration config = params.toConfiguration();
        assertEquals("pmd.filelist", config.getInputFile().toString());
        assertThat(config.getInputPathList(), empty()); // no additional input paths
    }
}
