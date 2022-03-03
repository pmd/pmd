/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.document;

import java.util.logging.Logger;

import net.sourceforge.pmd.util.log.SimplePmdLogger;

/**
 * @author Clément Fournier
 */
public class PmdTestLogger extends SimplePmdLogger {

    private static final Logger LOG = Logger.getLogger("testlogger");

    public PmdTestLogger() {
        super(LOG);
        setLevel(null);
    }
}
