/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.cli.PMDCommandLineInterface;
import net.sourceforge.pmd.cli.PMDParameters;

public class LanguageParameterTest {

    /** Test that language parameters from the CLI are correctly passed through to the PMDConfiguration. Although this is a
     * CLI test, it resides here to take advantage of {@link net.sourceforge.pmd.lang.DummyLanguageModule}
     */
    @Test
    public void testLanguageFromCliToConfiguration() {
        PMDParameters params = new PMDParameters();
        String[] args = { "-d", "source_folder", "-f", "ideaj", "-P", "sourcePath=/home/user/source/", "-R", "java-empty", "-force-language", "dummy"};
        PMDCommandLineInterface.extractParameters(params, args, "PMD");

        Assert.assertEquals(new DummyLanguageModule().getDefaultVersion().getName(), params.toConfiguration().getForceLanguageVersion().getName());
    }
}
