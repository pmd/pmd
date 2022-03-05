/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.document;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sourceforge.pmd.util.log.SimpleMessageReporter;

/**
 * @author Clément Fournier
 */
public class TestMessageReporter extends SimpleMessageReporter {

    private static final Logger LOG = LoggerFactory.getLogger(PmdTestLogger.class.getName());

    public TestMessageReporter() {
        super(LOG);
        setLevel(null);
    }
}
