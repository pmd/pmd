/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.scm;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.scm.invariants.PrintedMessageInvariant;
import net.sourceforge.pmd.scm.strategies.XPathStrategy;

public class ScmConfigurationTest {
    private static final String DEFAULT_GENERIC_USAGE_TEXT =
          "Usage: scm [options]\n"
        + "  Options:\n"
        + "    --charset\n"
        + "      Charset of the source file to be minimized\n"
        + "      Default: UTF-8\n"
        + "    --help\n"
        + "      Display help\n"
        + "  * --input-file\n"
        + "      Original file that should be minimized\n"
        + "    --invariant\n"
        + "      Invariant to preserve during minimization\n"
        + "      Default: dummy\n"
        + "  * --language\n"
        + "      Source code language\n"
        + "  * --output-file\n"
        + "      Output file (used as a scratch file, too)\n"
        + "  * --strategy\n"
        + "      Minimization strategy\n"
        + "Available languages: dummy\n";

    private static final String DEFAULT_DUMMY_USAGE_TEXT =
          "Usage: scm [options]\n"
        + "  Options:\n"
        + "    --charset\n"
        + "      Charset of the source file to be minimized\n"
        + "      Default: UTF-8\n"
        + "    --help\n"
        + "      Display help\n"
        + "  * --input-file\n"
        + "      Original file that should be minimized\n"
        + "    --invariant\n"
        + "      Invariant to preserve during minimization\n"
        + "      Default: dummy\n"
        + "  * --language\n"
        + "      Source code language\n"
        + "  * --output-file\n"
        + "      Output file (used as a scratch file, too)\n"
        + "  * --strategy\n"
        + "      Minimization strategy\n"
        + "Available languages: dummy\n"
        + "=== Parameters specific to language: dummy\n"
        + "--- Parameters specific to strategy: xpath\n"
        + "Usage:  [options]\n"
        + "  Options:\n"
        + "  * --xpath-expression\n"
        + "      XPath 2.0 expression to drop matched subtrees\n"
        + "--- Parameters specific to strategy: greedy\n"
        + "Usage:\n"
        + "--- Parameters specific to invariant: dummy\n"
        + "Usage:\n"
        + "--- Parameters specific to invariant: exitcode\n"
        + "Usage:  [options]\n"
        + "  Options:\n"
        + "  * --command-line\n"
        + "      Command line for running a compiler on a source to be minimized\n"
        + "    --exact-return\n"
        + "      Compiler should exit with this specific exit value only (implies min == \n"
        + "      max) \n"
        + "      Default: -1\n"
        + "    --max-return\n"
        + "      Maximum exit code value (inclusive)\n"
        + "      Default: 2147483647\n"
        + "    --min-return\n"
        + "      Minimum exit code value (inclusive)\n"
        + "      Default: 1\n"
        + "--- Parameters specific to invariant: message\n"
        + "Usage:  [options]\n"
        + "  Options:\n"
        + "  * --command-line\n"
        + "      Command line for running a compiler on a source to be minimized\n"
        + "  * --printed-message\n"
        + "      Message that should be printed by the compiler\n"
        + "    --printed-message-charset\n"
        + "      Charset of compiler output\n"
        + "      Default: UTF-8\n";

    @Test
    public void testGenericUsageText() {
        SCMConfiguration configuration = new SCMConfiguration();
        String[] args = { };
        boolean configurationParsed = configuration.parse(args);
        Assert.assertFalse(configurationParsed);
        Assert.assertEquals(
                "The following options are required: [--language], [--output-file], [--input-file], [--strategy]",
                configuration.getErrorString());
        Assert.assertEquals(DEFAULT_GENERIC_USAGE_TEXT, configuration.getHelpString());
    }

    @Test
    public void testDefaultUsageText() {
        SCMConfiguration configuration = new SCMConfiguration();
        String[] args = { "--language", "dummy" };
        boolean configurationParsed = configuration.parse(args);
        Assert.assertFalse(configurationParsed);
        Assert.assertEquals(
                "The following options are required: [--output-file], [--input-file], [--strategy]",
                configuration.getErrorString());
        Assert.assertEquals(DEFAULT_DUMMY_USAGE_TEXT, configuration.getHelpString());
    }

    @Test
    public void testSuccessfulParsing() {
        SCMConfiguration configuration = new SCMConfiguration();
        String[] args = { "--language", "dummy", "--strategy", "xpath", "--invariant", "message",
            "--input-file", "1", "--output-file", "2",
            "--command-line", "/bin/echo Internal compiler error",
            "--xpath-expression", "//Comment",
            "--printed-message", "Internal compiler error",
        };
        boolean success = configuration.parse(args);
        Assert.assertNull(configuration.getErrorString());
        Assert.assertTrue(success);

        Assert.assertTrue(configuration.getLanguageHandler() instanceof DummyLanguage);
        Assert.assertTrue(configuration.getStrategyConfig() instanceof XPathStrategy.Configuration);
        Assert.assertTrue(configuration.getInvariantCheckerConfig() instanceof PrintedMessageInvariant.Configuration);

        XPathStrategy.Configuration xpathConfig = (XPathStrategy.Configuration) configuration.getStrategyConfig();
        PrintedMessageInvariant.Configuration invariantConfig = (PrintedMessageInvariant.Configuration) configuration.getInvariantCheckerConfig();

        Assert.assertEquals("//Comment", xpathConfig.getExpression());
        Assert.assertEquals("Internal compiler error", invariantConfig.getMessage());
    }
}
