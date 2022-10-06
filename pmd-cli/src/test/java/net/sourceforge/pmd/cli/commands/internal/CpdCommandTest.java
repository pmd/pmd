/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli.commands.internal;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.cpd.CPDConfiguration;

class CpdCommandTest extends BaseCommandTest<CpdCommand> {

    @Test
    void testMultipleDirs() {
        final CpdCommand cmd = setupAndParse(
            "-d", "a", "b"
        );
        assertMultipleDirs(cmd);
    }

    @Test
    void testMultipleDirsWithCommas() {
        final CpdCommand cmd = setupAndParse(
            "-d", "a,b"
        );
        assertMultipleDirs(cmd);
    }

    @Test
    void testMultipleDirsWithRepeatedOption() {
        final CpdCommand cmd = setupAndParse(
            "-d", "a", "-d", "b"
        );
        assertMultipleDirs(cmd);
    }

    @Test
    void testNoPositionalParametersAllowed() {
        final CpdCommand cmd = setupAndParse(
            "-d", "a", "--", "b"
        );
        assertMultipleDirs(cmd);
    }

    @Test
    void testEmptyDirOption() {
        assertError("-d", "-f", "text");
    }

    private void assertMultipleDirs(final CpdCommand result) {
        final CPDConfiguration config = result.toConfiguration();
        assertEquals(listOf("a", "b"), config.getFiles().stream().map(File::toString).collect(Collectors.toList()));
    }

    @Override
    protected CpdCommand createCommand() {
        return new CpdCommand();
    }

    @Override
    protected void addStandardParams(final List<String> argList) {
        // If no minimum tokens provided, set default value
        if (!argList.contains("--minimum-tokens")) {
            argList.add(0, "--minimum-tokens");
            argList.add(1, "100");
        }
    }
}
