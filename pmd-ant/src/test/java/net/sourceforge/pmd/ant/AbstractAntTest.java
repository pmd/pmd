/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.ant;

import static com.github.stefanbirkner.systemlambda.SystemLambda.restoreSystemProperties;

import java.io.File;
import java.io.PrintStream;
import java.io.StringWriter;
import java.nio.charset.Charset;

import org.apache.tools.ant.BuildEvent;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.BuildListener;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.ProjectHelper;
import org.junit.jupiter.api.AfterAll;

import net.sourceforge.pmd.internal.Slf4jSimpleConfiguration;
import net.sourceforge.pmd.internal.util.IOUtil;

class AbstractAntTest {
    protected Project project;

    protected StringBuilder log;

    protected StringWriter out;
    protected StringWriter err;

    protected void configureProject(String filename) {
        project = new Project();
        project.init();
        project.addBuildListener(new AntBuildListener(Project.MSG_INFO));

        File antFile = new File(filename);
        ProjectHelper.configureProject(project, antFile);
    }

    @AfterAll
    static void resetLogging() {
        Slf4jSimpleConfiguration.reconfigureDefaultLogLevel(null);
    }

    protected void executeTarget(String targetName) {
        // restoring system properties: PMDTask might change logging properties
        // See Slf4jSimpleConfigurationForAnt and resetLogging
        try {
            restoreSystemProperties(() -> {
                executeTargetImpl(targetName);
            });
        } catch (BuildException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void executeTargetImpl(String targetName) {
        log = new StringBuilder();
        out = new StringWriter();
        err = new StringWriter();
        PrintStream outStream = new PrintStream(IOUtil.fromWriter(out, Charset.defaultCharset().name()));
        PrintStream errStream = new PrintStream(IOUtil.fromWriter(err, Charset.defaultCharset().name()));
        synchronized (System.out) {
            PrintStream originalOut = System.out;
            PrintStream originalErr = System.err;
            originalOut.flush();
            originalErr.flush();
            try {
                System.setOut(outStream);
                System.setErr(errStream);
                project.executeTarget(targetName);
            } finally {
                System.setOut(originalOut);
                System.setErr(originalErr);
            }
        }
    }

    private class AntBuildListener implements BuildListener {
        private final int logLevel;

        private AntBuildListener(int logLevel) {
            this.logLevel = logLevel;
        }


        @Override
        public void buildStarted(BuildEvent event) {
        }

        @Override
        public void buildFinished(BuildEvent event) {
        }

        @Override
        public void targetStarted(BuildEvent event) {
        }

        @Override
        public void targetFinished(BuildEvent event) {
        }

        @Override
        public void taskStarted(BuildEvent event) {
        }

        @Override
        public void taskFinished(BuildEvent event) {
        }

        @Override
        public void messageLogged(BuildEvent event) {
            if (event.getPriority() > logLevel) {
                return;
            }
            log.append(event.getMessage());
        }
    }
}
