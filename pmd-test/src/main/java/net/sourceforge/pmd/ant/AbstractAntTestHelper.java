/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.ant;

import static java.io.File.separator;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;

import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.DefaultLogger;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

import net.sourceforge.pmd.internal.Slf4jSimpleConfiguration;

import com.github.stefanbirkner.systemlambda.Statement;
import com.github.stefanbirkner.systemlambda.SystemLambda;

/**
 * Base test class for ant tests.
 *
 * <p>Usage template:
 *
 * <pre>
 * {@code
 * class MyPMDTaskTest extends AbstractAntTestHelper {
 *     MyPMDTaskTest() {
 *          antTestScriptFilename = "mypmdtasktest.xml";
 *     }
 *
 *     @Test
 *     void myTest() {
 *         executeTarget("testMyTarget");
 *         assertOutputContaining("Expected Violation Message");
 *     }
 * }
 * }
 * </pre>
 *
 * @author Romain Pelisse &lt;belaran@gmail.com&gt;
 *
 */
public abstract class AbstractAntTestHelper {

    @TempDir
    private Path tempFolder;

    protected String pathToTestScript;
    protected String antTestScriptFilename;

    private Project antProject;
    private StringBuilder log = new StringBuilder();
    private String output;

    public AbstractAntTestHelper() {
        pathToTestScript = "target/test-classes/net/sourceforge/pmd/ant/xml";
    }

    @BeforeEach
    public void setUp() throws IOException {
        validatePostConstruct();
        // initialize Ant
        antProject = new Project();
        antProject.init();
        antProject.addBuildListener(new AntBuildListener());
        ProjectHelper.configureProject(antProject, new File(pathToTestScript + separator + antTestScriptFilename));

        // Each test case gets one temp file name, accessible with property ${tmpfile}
        Path tmpFile = Files.createTempFile(tempFolder, "pmd-ant-tests", null);
        // we delete the tmpfile again, since we only wanted to have a unique temp filename
        // the tmpfile is used for creating reports.
        Files.deleteIfExists(tmpFile);
        antProject.setProperty("tmpfile", tmpFile.toAbsolutePath().toString());
    }

    @AfterAll
    static void resetLogging() {
        Slf4jSimpleConfiguration.reconfigureDefaultLogLevel(null);
    }

    /**
     * Returns the current temporary file. Replaced by a fresh (inexistent)
     * file before each test.
     */
    public File currentTempFile() {
        String tmpname = antProject.getProperty("tmpfile");
        return tmpname == null ? null : new File(tmpname);
    }


    private void validatePostConstruct() {
        if (pathToTestScript == null || "".equals(pathToTestScript) || antTestScriptFilename == null
                || "".equals(antTestScriptFilename)) {
            throw new IllegalStateException("Unit tests for Ant script badly initialized");
        }
    }

    public String executeTarget(String target) {
        try {
            restoreLocale(() -> {
                // restoring system properties: Test might change file.encoding or might change logging properties
                // See Slf4jSimpleConfigurationForAnt and resetLogging
                SystemLambda.restoreSystemProperties(() -> {
                    output = tapSystemOut(() -> {
                        antProject.executeTarget(target);
                    });
                });
            });
            return output;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected String getLog() {
        return log.toString();
    }

    public void assertOutputContaining(String text) {
        assertThat(output, containsString(text));
    }


    public void assertContains(String text, String toFind) {
        assertThat(text, containsString(toFind));
    }


    public void assertDoesntContain(String text, String toFind) {
        assertThat(text, not(containsString(toFind)));
    }

    private static void restoreLocale(Statement statement) throws Exception {
        Locale originalLocale = Locale.getDefault();
        try {
            statement.execute();
        } finally {
            Locale.setDefault(originalLocale);
        }
    }

    /**
     * This is similar to {@link SystemLambda#tapSystemOut(Statement)}. But this
     * method doesn't use the platform default charset as it was when the JVM started.
     * Instead, it uses the current system property {@code file.encoding}. This allows
     * tests to change the encoding.
     *
     * @param statement an arbitrary piece of code.
     * @return text that is written to stdout. Lineendings are normalized to {@code \n}.
     * @throws Exception any exception thrown by the statement
     */
    private static String tapSystemOut(Statement statement) throws Exception {
        @SuppressWarnings("PMD.CloseResource") // we don't want to close System.out
        PrintStream originalOut = System.out;
        ByteArrayOutputStream text = new ByteArrayOutputStream();
        String currentDefaultCharset = System.getProperty("file.encoding");
        try {
            PrintStream replacement = new PrintStream(text, true, currentDefaultCharset);
            System.setOut(replacement);
            statement.execute();
        } finally {
            System.setOut(originalOut);
        }
        String result = text.toString(currentDefaultCharset);
        return result.replace(System.lineSeparator(), "\n");
    }

    private final class AntBuildListener extends DefaultLogger {
        private AntBuildListener() {
            msgOutputLevel = Project.MSG_INFO;
        }

        @Override
        protected void printMessage(String message, PrintStream stream, int priority) {
            log.append(message);
        }

        @Override
        public void messageLogged(BuildEvent buildEvent) {
            if (buildEvent.getPriority() <= Project.MSG_INFO) {
                log.append(buildEvent.getMessage());
            }
        }
    }
}
