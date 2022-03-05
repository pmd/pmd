/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.document;

import java.util.logging.Logger;

import net.sourceforge.pmd.util.log.internal.SimpleMessageReporter;

/**
 * @author Clément Fournier
 */
public class TestMessageReporter extends SimpleMessageReporter {

    private static final Logger LOG = Logger.getLogger("testlogger");

    public TestMessageReporter() {
        super(LOG);
        setLevel(null);
    }
}
