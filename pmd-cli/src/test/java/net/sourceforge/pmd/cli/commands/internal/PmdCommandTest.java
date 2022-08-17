/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli.commands.internal;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.cli.PmdParametersParseResult;

import picocli.CommandLine;
import picocli.CommandLine.ParseResult;

class PmdCommandTest {

//    @Test
//    void testVersion() throws Exception {
//        PMDParameters parameters = new PMDParameters();
//        // no language set, uses default language
//        assertEquals("1.7", parameters.getVersion());
//
//        // now set language
//        FieldUtils.writeDeclaredField(parameters, "language", "dummy2", true);
//        assertEquals("1.0", parameters.getVersion());
//    }

    @Test
    void testMultipleDirsAndRuleSets() {
        final PmdCommand cmd = setupAndParse(
            "-d", "a", "b", "-R", "x.xml", "y.xml"
        );
        assertMultipleDirsAndRulesets(cmd);
    }

    @Test
    void testMultipleDirsAndRuleSetsWithCommas() {
        final PmdCommand cmd = setupAndParse(
            "-d", "a,b", "-R", "x.xml,y.xml"
        );
        assertMultipleDirsAndRulesets(cmd);
    }

    @Test
    void testMultipleDirsAndRuleSetsWithRepeatedOption() {
        final PmdCommand cmd = setupAndParse(
            "-d", "a", "-d", "b", "-R", "x.xml", "-R", "y.xml"
        );
        assertMultipleDirsAndRulesets(cmd);
    }

    @Test
    void testNoPositionalParametersAllowed() {
        assertError(
            //                        vvvv
            "-R", "x.xml", "-d", "a", "--", "-d", "b"
        );
    }


    private void assertMultipleDirsAndRulesets(final PmdCommand result) {
        final PMDConfiguration config = result.toConfiguration();
        assertEquals(listOf("a", "b"), config.getAllInputPaths());
        assertEquals(listOf("x.xml", "y.xml"), config.getRuleSetPaths());
    }

    @Test
    void testEmptyDirOption() {
        assertError("-d", "-R", "y.xml");
    }

    @Test
    void testEmptyRulesetOption() {
        assertError("-R", "-d", "something");
    }

    private void assertError(String... params) {
        // TODO
        PmdParametersParseResult result = PmdParametersParseResult.extractParameters(params);
        assertTrue(result.isError());
    }

    private PmdCommand setupAndParse(final String... params) {
        final PmdCommand cmd = new PmdCommand();
        
        // Always run against dummy language
        final List<String> argList = new ArrayList<>();
        argList.add("--use-version");
        argList.add("dummy");
        argList.addAll(Arrays.asList(params));

        ParseResult parseResult = new CommandLine(cmd)
            .setCaseInsensitiveEnumValuesAllowed(true)
            .parseArgs(argList.toArray(new String[0]));

        assertThat(parseResult.errors(), Matchers.empty());

        return cmd;
    }
}
