/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.github.stefanbirkner.systemlambda.SystemLambda;

class CPDCommandLineInterfaceTest {
    private static final String SRC_DIR = "src/test/resources/net/sourceforge/pmd/cpd/files/";

    @TempDir
    public Path tempDir;


    @BeforeEach
    void setup() {
        System.setProperty(CPDCommandLineInterface.NO_EXIT_AFTER_RUN, "true");
    }
    
    @Test
    void testEmptyResultRendering() throws Exception {
        String stdout = SystemLambda.tapSystemOut(() -> {
            SystemLambda.tapSystemErr(() -> {
                CPDCommandLineInterface.main(new String[] { "--minimum-tokens", "340", "--language", "java", "--files",
                    SRC_DIR, "--format", "xml", });
            });
        });
        Assertions.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\n" + "<pmd-cpd/>", stdout.trim());
    }

    @Test
    void testDeprecatedOptionsWarning() throws Exception {
        File filelist = new File(tempDir.toFile(), "cpd-test-file-list.txt");
        Files.write(filelist.toPath(), Arrays.asList(
                new File(SRC_DIR, "dup1.java").getAbsolutePath(),
                new File(SRC_DIR, "dup2.java").getAbsolutePath()), StandardCharsets.UTF_8);

        String stderr = SystemLambda.tapSystemErr(() -> {
            String stdout = SystemLambda.tapSystemOut(() -> {
                CPDCommandLineInterface.main(new String[] { "--minimum-tokens", "340", "--language", "java", "--filelist",
                    filelist.getAbsolutePath(), "--format", "xml", "-failOnViolation", "true" });
            });
            Assertions.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "\n" + "<pmd-cpd/>", stdout.trim());
        });
        Assertions.assertTrue(stderr.contains("Some deprecated options were used on the command-line, including -failOnViolation"));
        Assertions.assertTrue(stderr.contains("Consider replacing it with --fail-on-violation"));
        // only one parameter is logged
        Assertions.assertFalse(stderr.contains("Some deprecated options were used on the command-line, including --filelist"));
        Assertions.assertFalse(stderr.contains("Consider replacing it with --file-list"));
    }
}
