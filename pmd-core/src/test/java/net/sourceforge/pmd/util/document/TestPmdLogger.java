/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.util.document;

import java.util.logging.Level;
import java.util.logging.Logger;

import net.sourceforge.pmd.util.log.SimplePmdLogger;

/**
 * @author Clément Fournier
 */
public class TestPmdLogger extends SimplePmdLogger {

    private static final Logger LOG = Logger.getLogger("testlogger");

    static {
        LOG.setLevel(Level.OFF);
    }

    public TestPmdLogger() {
        super(LOG);
    }
}
