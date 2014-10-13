/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.cli;

import static org.junit.Assert.assertTrue;

import java.io.File;

import net.sourceforge.pmd.util.FileUtil;

import org.junit.Test;

/**
 * @author Romain Pelisse <belaran@gmail.com>
 * 
 */
public class CLITest extends BaseCLITest {
    @Test
    public void useEcmaScript() {
        String[] args = { "-d", SOURCE_FOLDER, "-f", "xml", "-R", "ecmascript-basic", "-version", "3", "-l",
                "ecmascript", "-debug" };
        String resultFilename = runTest(args, "useEcmaScript");
        assertTrue("Invalid Java version",
                FileUtil.findPatternInFile(new File(resultFilename), "Using Ecmascript version: Ecmascript 3"));
    }
}
