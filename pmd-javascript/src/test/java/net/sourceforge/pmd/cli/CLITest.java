/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cli;

import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import net.sourceforge.pmd.util.FileUtil;

/**
 * @author Romain Pelisse &lt;belaran@gmail.com&gt;
 *
 */
public class CLITest extends BaseCLITest {
    @Test
    public void useEcmaScript() {
        String[] args = { "-d", SOURCE_FOLDER, "-f", "xml", "-R", "ecmascript-basic", "-version", "3", "-l",
            "ecmascript", "-debug", };
        String resultFilename = runTest(args, "useEcmaScript");
        assertTrue("Invalid JavaScript version",
                FileUtil.findPatternInFile(new File(resultFilename), "Using Ecmascript version: Ecmascript 3"));
    }
}
