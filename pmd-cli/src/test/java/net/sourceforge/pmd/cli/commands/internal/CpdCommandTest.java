/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli.commands.internal;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

import net.sourceforge.pmd.cpd.CPDConfiguration;
import net.sourceforge.pmd.util.CollectionUtil;

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
        assertEquals(listOf("a", "b"), CollectionUtil.map(config.getInputPathList(), Path::toString));
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
