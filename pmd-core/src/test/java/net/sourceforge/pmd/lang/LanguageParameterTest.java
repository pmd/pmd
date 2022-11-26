/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.cli.PMDCommandLineInterface;
import net.sourceforge.pmd.cli.PMDParameters;

class LanguageParameterTest {

    /** Test that language parameters from the CLI are correctly passed through to the PMDConfiguration. Although this is a
     * CLI test, it resides here to take advantage of {@link net.sourceforge.pmd.lang.DummyLanguageModule}
     */
    @Test
    void testLanguageFromCliToConfiguration() {
        PMDParameters params = new PMDParameters();
        String[] args = { "-d", "source_folder", "-f", "ideaj", "-P", "sourcePath=/home/user/source/", "-R", "java-empty", "-force-language", "dummy"};
        PMDCommandLineInterface.extractParameters(params, args, "PMD");

        assertEquals(DummyLanguageModule.getInstance().getDefaultVersion().getName(), params.toConfiguration().getForceLanguageVersion().getName());
    }
}
