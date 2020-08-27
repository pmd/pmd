/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.io.StringReader;

import com.beust.jcommander.JCommander;

import org.apache.commons.io.IOUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.ComparisonFailure;
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

    static void assertStringEqualsIgnoreEOL(String expected, String actual) throws IOException {
        try (StringReader expectedReader = new StringReader(expected);
             StringReader actualReader = new StringReader(actual);
        ) {
            if (!IOUtils.contentEqualsIgnoreEOL(expectedReader, actualReader)) {
                throw new ComparisonFailure("", expected, actual);
            }
        }
    }

    @Test
    public void testBuildUsageText() throws IOException {
        String string1 = PMDCommandLineInterface.buildUsageText();
        String string2 = PMDCommandLineInterface.buildUsageText(null);
        Assert.assertEquals(string1, string2);
        assertStringEqualsIgnoreEOL(TEST_BUILD_USAGE_TEXT_EXPECTED_1, string1);
        String ta = "a";
        String tb = "c";
        Object tc = new Object();
        JCommander jcommander = new JCommander();
        jcommander.addCommand(ta, tc);
        jcommander.addCommand(tb, tc);
        String string3 = PMDCommandLineInterface.buildUsageText(jcommander);
        assertStringEqualsIgnoreEOL(TEST_BUILD_USAGE_TEXT_EXPECTED_2, string3);
    }

    static final String TEST_BUILD_USAGE_TEXT_EXPECTED_1 = "\n"
            + "Mandatory arguments:\n"
            + "1) A java source code filename or directory\n"
            + "2) A report format \n"
            + "3) A ruleset filename or a comma-delimited string of ruleset filenames\n"
            + "\n"
            + "For example: \n"
            + "C:\\>pmd-bin-unknown\\bin\\pmd.bat -d c:\\my\\source\\code -f html -R java-unusedcode\n"
            + "\n"
            + "Languages and version suported:\n"
            + "dummy, dummy2\n"
            + "\n"
            + "Available report formats and their configuration properties are:\n"
            + "   codeclimate: Code Climate integration.\n"
            + "   csv: Comma-separated values tabular format.\n"
            + "        problem - Include Problem column   default: true\n"
            + "        package - Include Package column   default: true\n"
            + "        file - Include File column   default: true\n"
            + "        priority - Include Priority column   default: true\n"
            + "        line - Include Line column   default: true\n"
            + "        desc - Include Description column   default: true\n"
            + "        ruleSet - Include Rule set column   default: true\n"
            + "        rule - Include Rule column   default: true\n"
            + "   emacs: GNU Emacs integration.\n"
            + "   empty: Empty, nothing.\n"
            + "   html: HTML format\n"
            + "        linePrefix - Prefix for line number anchor in the source file.\n"
            + "        linkPrefix - Path to HTML source.\n"
            + "        htmlExtension - Replace file extension with .html for the links (default: false)   default: "
            + "false\n"
            + "   ideaj: IntelliJ IDEA integration.\n"
            + "        classAndMethodName - Class and Method name, pass '.method' when processing a directory.   "
            + "default: \n"
            + "        sourcePath - Source path.   default: \n"
            + "        fileName - File name.   default: \n"
            + "   json: JSON format.\n"
            + "   summaryhtml: Summary HTML format.\n"
            + "        linePrefix - Prefix for line number anchor in the source file.\n"
            + "        linkPrefix - Path to HTML source.\n"
            + "        htmlExtension - Replace file extension with .html for the links (default: false)   default: "
            + "false\n"
            + "   text: Text format.\n"
            + "   textcolor: Text format, with color support (requires ANSI console support, e.g. xterm, rxvt, etc"
            + ".).\n"
            + "        color - Enables colors with anything other than 'false' or '0'.   default: yes\n"
            + "   textpad: TextPad integration.\n"
            + "   vbhtml: Vladimir Bossicard HTML format.\n"
            + "   xml: XML format.\n"
            + "        encoding - XML encoding format, defaults to UTF-8.   default: UTF-8\n"
            + "   xslt: XML with a XSL Transformation applied.\n"
            + "        encoding - XML encoding format, defaults to UTF-8.   default: UTF-8\n"
            + "        xsltFilename - The XSLT file name.\n"
            + "   yahtml: Yet Another HTML format.\n"
            + "        outputDir - Output directory.\n"
            + "\n"
            + "For example on windows: \n"
            + "C:\\>pmd-bin-unknown\\bin\\pmd.bat -dir c:\\my\\source\\code -format text -R "
            + "rulesets/java/quickstart.xml -version 1.5 -language java -debug\n"
            + "C:\\>pmd-bin-unknown\\bin\\pmd.bat -dir c:\\my\\source\\code -f xml -rulesets "
            + "rulesets/java/quickstart.xml,category/java/codestyle.xml -encoding UTF-8\n"
            + "C:\\>pmd-bin-unknown\\bin\\pmd.bat -d c:\\my\\source\\code -rulesets rulesets/java/quickstart.xml "
            + "-auxclasspath lib\\commons-collections.jar;lib\\derby.jar\n"
            + "C:\\>pmd-bin-unknown\\bin\\pmd.bat -d c:\\my\\source\\code -f html -R rulesets/java/quickstart.xml "
            + "-auxclasspath file:///C:/my/classpathfile\n"
            + "\n"
            + "For example on *nix: \n"
            + "$ pmd-bin-unknown/bin/run.sh pmd -dir /home/workspace/src/main/java/code -f html -rulesets "
            + "rulesets/java/quickstart.xml,category/java/codestyle.xml\n"
            + "$ pmd-bin-unknown/bin/run.sh pmd -d ./src/main/java/code -R rulesets/java/quickstart.xml -f xslt "
            + "-property xsltFilename=my-own.xsl\n"
            + "$ pmd-bin-unknown/bin/run.sh pmd -d ./src/main/java/code -f html -R rulesets/java/quickstart.xml "
            + "-auxclasspath commons-collections.jar:derby.jar\n"
            + "\n"
            + "\n"
            + "\n";

    static final String TEST_BUILD_USAGE_TEXT_EXPECTED_2 = "\n"
            + "Mandatory arguments:\n"
            + "1) A java source code filename or directory\n"
            + "2) A report format \n"
            + "3) A ruleset filename or a comma-delimited string of ruleset filenames\n"
            + "\n"
            + "For example: \n"
            + "C:\\>pmd-bin-unknown\\bin\\pmd.bat -d c:\\my\\source\\code -f html -R java-unusedcode\n"
            + "\n"
            + "Languages and version suported:\n"
            + "dummy, dummy2\n"
            + "\n"
            + "Optional arguments that may be put before or after the mandatory arguments: \n"
            + "null\n"
            + "null\n"
            + "\n"
            + "Available report formats and their configuration properties are:\n"
            + "   codeclimate: Code Climate integration.\n"
            + "   csv: Comma-separated values tabular format.\n"
            + "        problem - Include Problem column   default: true\n"
            + "        package - Include Package column   default: true\n"
            + "        file - Include File column   default: true\n"
            + "        priority - Include Priority column   default: true\n"
            + "        line - Include Line column   default: true\n"
            + "        desc - Include Description column   default: true\n"
            + "        ruleSet - Include Rule set column   default: true\n"
            + "        rule - Include Rule column   default: true\n"
            + "   emacs: GNU Emacs integration.\n"
            + "   empty: Empty, nothing.\n"
            + "   html: HTML format\n"
            + "        linePrefix - Prefix for line number anchor in the source file.\n"
            + "        linkPrefix - Path to HTML source.\n"
            + "        htmlExtension - Replace file extension with .html for the links (default: false)   default: "
            + "false\n"
            + "   ideaj: IntelliJ IDEA integration.\n"
            + "        classAndMethodName - Class and Method name, pass '.method' when processing a directory.   "
            + "default: \n"
            + "        sourcePath - Source path.   default: \n"
            + "        fileName - File name.   default: \n"
            + "   json: JSON format.\n"
            + "   summaryhtml: Summary HTML format.\n"
            + "        linePrefix - Prefix for line number anchor in the source file.\n"
            + "        linkPrefix - Path to HTML source.\n"
            + "        htmlExtension - Replace file extension with .html for the links (default: false)   default: "
            + "false\n"
            + "   text: Text format.\n"
            + "   textcolor: Text format, with color support (requires ANSI console support, e.g. xterm, rxvt, etc"
            + ".).\n"
            + "        color - Enables colors with anything other than 'false' or '0'.   default: yes\n"
            + "   textpad: TextPad integration.\n"
            + "   vbhtml: Vladimir Bossicard HTML format.\n"
            + "   xml: XML format.\n"
            + "        encoding - XML encoding format, defaults to UTF-8.   default: UTF-8\n"
            + "   xslt: XML with a XSL Transformation applied.\n"
            + "        encoding - XML encoding format, defaults to UTF-8.   default: UTF-8\n"
            + "        xsltFilename - The XSLT file name.\n"
            + "   yahtml: Yet Another HTML format.\n"
            + "        outputDir - Output directory.\n"
            + "\n"
            + "For example on windows: \n"
            + "C:\\>pmd-bin-unknown\\bin\\pmd.bat -dir c:\\my\\source\\code -format text -R "
            + "rulesets/java/quickstart.xml -version 1.5 -language java -debug\n"
            + "C:\\>pmd-bin-unknown\\bin\\pmd.bat -dir c:\\my\\source\\code -f xml -rulesets "
            + "rulesets/java/quickstart.xml,category/java/codestyle.xml -encoding UTF-8\n"
            + "C:\\>pmd-bin-unknown\\bin\\pmd.bat -d c:\\my\\source\\code -rulesets rulesets/java/quickstart.xml "
            + "-auxclasspath lib\\commons-collections.jar;lib\\derby.jar\n"
            + "C:\\>pmd-bin-unknown\\bin\\pmd.bat -d c:\\my\\source\\code -f html -R rulesets/java/quickstart.xml "
            + "-auxclasspath file:///C:/my/classpathfile\n"
            + "\n"
            + "For example on *nix: \n"
            + "$ pmd-bin-unknown/bin/run.sh pmd -dir /home/workspace/src/main/java/code -f html -rulesets "
            + "rulesets/java/quickstart.xml,category/java/codestyle.xml\n"
            + "$ pmd-bin-unknown/bin/run.sh pmd -d ./src/main/java/code -R rulesets/java/quickstart.xml -f xslt "
            + "-property xsltFilename=my-own.xsl\n"
            + "$ pmd-bin-unknown/bin/run.sh pmd -d ./src/main/java/code -f html -R rulesets/java/quickstart.xml "
            + "-auxclasspath commons-collections.jar:derby.jar\n"
            + "\n"
            + "\n"
            + "\n";
}
