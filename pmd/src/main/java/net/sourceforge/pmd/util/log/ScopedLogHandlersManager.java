/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.util.log;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *	@author Romain PELISSE, belaran@gmail.com
 *  @author Roberto Ferranti - rferranti@users.sourceforge.net
 *  Thanks to Jesse Glick for the bug report.
 *
 */
public class ScopedLogHandlersManager {

	private static final String PACKAGE_NAME = "net.sourceforge.pmd";

    @SuppressWarnings("PMD.LoggerIsNotStaticFinal")
    private Logger logger;
    private Level oldLogLevel;
    private Handler[] oldHandlers;
    private Handler[] newHandlers;

    public ScopedLogHandlersManager(Level level, Handler... handlers) {
        newHandlers = handlers;
        logger = Logger.getLogger(PACKAGE_NAME);
        oldHandlers = logger.getHandlers();
        oldLogLevel = logger.getLevel();
        logger.setLevel(level);
        //The Ant logger filters itself
        for (Handler handler : oldHandlers) {
            logger.removeHandler(handler);
        }
        for (Handler handler : newHandlers) {
            logger.addHandler(handler);
        }
    }

    public void close() {
        for (Handler handler : newHandlers) {
            logger.removeHandler(handler);
        }
        for (Handler handler : oldHandlers) {
            logger.addHandler(handler);
        }
        logger.setLevel(oldLogLevel);
    }
}
