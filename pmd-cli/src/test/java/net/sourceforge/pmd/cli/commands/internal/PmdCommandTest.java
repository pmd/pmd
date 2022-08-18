/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli.commands.internal;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.PMDConfiguration;
import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.LanguageVersion;

class PmdCommandTest extends BaseCommandTest<PmdCommand> {

    @Test
    void testVersionDefault() throws Exception {
        final PmdCommand cmd = setupAndParse("--use-version", "dummy", "-d", "a", "-R", "x.xml");
        final LanguageVersion dummyLatest = cmd.toConfiguration().getLanguageVersionOfFile("foo.dummy");
        
        // LanguageVersion do not implement equals, but we can check their string representations
        assertEquals(DummyLanguageModule.getInstance().getDefaultVersion().toString(), dummyLatest.toString());
    }

    @Test
    void testVersionLatest() throws Exception {
        final PmdCommand cmd = setupAndParse("--use-version", "dummy-latest", "-d", "a", "-R", "x.xml");
        final LanguageVersion dummyLatest = cmd.toConfiguration().getLanguageVersionOfFile("foo.dummy");
        
        // LanguageVersion do not implement equals, but we can check their string representations
        assertEquals(DummyLanguageModule.getInstance().getDefaultVersion().toString(), dummyLatest.toString());
    }

    @Test
    void testVersionGiven() throws Exception {
        final PmdCommand cmd = setupAndParse("--use-version", "dummy-1.2", "-d", "a", "-R", "x.xml");
        final LanguageVersion dummyLatest = cmd.toConfiguration().getLanguageVersionOfFile("foo.dummy");
        
        // LanguageVersion do not implement equals, but we can check their string representations
        assertEquals(DummyLanguageModule.getInstance().getVersion("1.2").toString(), dummyLatest.toString());
    }

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

    @Test
    void testEmptyDirOption() {
        assertError("-d", "-R", "y.xml");
    }

    @Test
    void testEmptyRulesetOption() {
        assertError("-R", "-d", "something");
    }

    private void assertMultipleDirsAndRulesets(final PmdCommand result) {
        final PMDConfiguration config = result.toConfiguration();
        assertEquals(listOf("a", "b"), config.getAllInputPaths());
        assertEquals(listOf("x.xml", "y.xml"), config.getRuleSetPaths());
    }

    @Override
    protected PmdCommand createCommand() {
        return new PmdCommand();
    }

    @Override
    protected void addStandardParams(final List<String> argList) {
        // If no language provided, set dummy latest
        if (!argList.contains("--use-version")) {
            argList.add("--use-version");
            argList.add("dummy");
        }
    }
}
