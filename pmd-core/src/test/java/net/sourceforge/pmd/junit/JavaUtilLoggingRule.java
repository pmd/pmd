/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.junit;

import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;
import java.util.logging.StreamHandler;

import org.apache.commons.io.output.ByteArrayOutputStream;
import org.junit.rules.ExternalResource;

/**
 * Junit Rule, to check for java util logging statements.
 *
 * @author Andreas Dangel
 * @see <a href="http://blog.diabol.se/?p=474">Testing the presence of log messages with java.util.logging</a>
 */
public class JavaUtilLoggingRule extends ExternalResource {
    private final Logger logger;
    private final ByteArrayOutputStream stream;
    private final StreamHandler customLogHandler;


    /**
     * Creates a new rule, that attaches a custom log handler
     * to the given logger.
     * @param loggerName the name of the logger to check
     */
    public JavaUtilLoggingRule(String loggerName) {
        this.logger = Logger.getLogger(loggerName);
        this.stream = new ByteArrayOutputStream();

        Logger currentLogger = logger;
        while (currentLogger.getHandlers().length == 0) {
            currentLogger = currentLogger.getParent();
        }
        this.customLogHandler = new StreamHandler(stream, currentLogger.getHandlers()[0].getFormatter());
    }

    @Override
    protected void before() throws Throwable {
        logger.addHandler(customLogHandler);
    }

    @Override
    protected void after() {
        logger.removeHandler(customLogHandler);
    }

    /**
     * Gets the complete log.
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
}
