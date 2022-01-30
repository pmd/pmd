/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.test.util;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.emptyString;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.MultipleFailureException;
import org.junit.runners.model.Statement;

/**
 * Junit Rule, to check for java util logging statements.
 * The log is only printed to console if the test fails.
 *
 * @author Andreas Dangel
 * @author Clément Fournier
 * @see <a href="http://blog.diabol.se/?p=474">Testing the presence of log messages with java.util.logging</a>
 */
public class JavaUtilLoggingRule implements TestRule {
    // copied from pmd core test sources

    private final Logger logger;
    private final ByteArrayOutputStream stream;
    private final StreamHandler customLogHandler;


    /**
     * Creates a new rule, that attaches a custom log handler
     * to the given logger.
     *
     * @param loggerName the name of the logger to check
     */
    public JavaUtilLoggingRule(String loggerName) {
        this(Logger.getLogger(loggerName));
    }

    /**
     * Creates a new rule, that attaches a custom log handler
     * to the given logger.
     *
     * @param logger the logger
     */
    public JavaUtilLoggingRule(Logger logger) {
        this.logger = logger;
        this.stream = new ByteArrayOutputStream();

        Logger currentLogger = logger;
        while (currentLogger.getHandlers().length == 0) {
            currentLogger = currentLogger.getParent();
        }
        Handler originalHandler = currentLogger.getHandlers()[0];
        Formatter formatter = originalHandler.getFormatter();
        logger.removeHandler(originalHandler); // disable printing to console
        this.customLogHandler = new StreamHandler(stream, formatter);
    }

    protected void before() {
        logger.addHandler(customLogHandler);
    }

    @Override
    @SuppressWarnings("PMD.AvoidCatchingThrowable")
    public Statement apply(Statement base, Description description) {
        return new Statement() {
            // this is a copy of the Statement in the ExternalResource base TestRule class,
            // except it prints the log if there was an error
            @Override
            public void evaluate() throws Throwable {
                before();

                List<Throwable> errors = new ArrayList<>();
                try {
                    base.evaluate();
                } catch (Throwable t) {
                    errors.add(t);
                } finally {
                    try {
                        after();
                    } catch (Throwable t) {
                        errors.add(t);
                    }
                }
                // this statement is the only difference
                if (!errors.isEmpty()) {
                    System.err.println(getLog());
                }

                MultipleFailureException.assertEmpty(errors);
            }
        };
    }

    protected void after() {
        logger.removeHandler(customLogHandler);
    }

    /**
     * Gets the complete log.
     *
     * @return the log
     */
    public String getLog() {
        customLogHandler.flush();
        return stream.toString(StandardCharsets.UTF_8);
    }

    /**
     * Clears the log.
     */
    public void clear() {
        customLogHandler.flush();
        stream.reset();
    }

    public void assertEmpty() {
        assertThat("Log output should be empty", getLog(), is(emptyString()));
    }

    public void assertContainsIgnoringCase(String str) {
        assertThat(getLog(), containsStringIgnoringCase(str));
    }
}
