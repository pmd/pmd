/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli;

import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

/**
 * @author Romain Pelisse &lt;belaran@gmail.com&gt;
 *
 */
public class CLITest extends BaseCLITest {
    @Test
    public void useEcmaScript() {
        String[] args = { "-d", SOURCE_FOLDER, "-f", "xml", "-R", "ecmascript-basic", "-l",
            "ecmascript", "--debug", };
        String log = runTest(args);
        assertThat(log, containsPattern("Adding file .*\\.js \\(lang: ecmascript ES6\\)"));
    }
}
