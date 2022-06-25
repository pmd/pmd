/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli;

import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

import net.sourceforge.pmd.PMD.StatusCode;

/**
 * @author Romain Pelisse &lt;belaran@gmail.com&gt;
 *
 */
public class CLITest extends BaseCLITest {
    @Test
    public void useEcmaScript() {
        String log = runTest(StatusCode.VIOLATIONS_FOUND,
                             "-d",
                             SOURCE_FOLDER,
                             "-f",
                             "xml",
                             "-R",
                             "rulesets/testing/js-rset1.xml",
                             "-l",
                             "ecmascript",
                             "--debug");
        assertThat(log, containsPattern("Adding file .*\\.js \\(lang: ecmascript ES6\\)"));
    }
}
