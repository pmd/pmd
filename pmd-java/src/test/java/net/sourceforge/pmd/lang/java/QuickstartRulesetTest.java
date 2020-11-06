/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java;

import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.junit.After;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemErrRule;

import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.RuleSetNotFoundException;
import net.sourceforge.pmd.util.ResourceLoader;

public class QuickstartRulesetTest {

    @Rule
    public final SystemErrRule systemErrRule = new SystemErrRule().enableLog().muteForSuccessfulTests();

    @After
    public void cleanup() {
        Handler[] handlers = Logger.getLogger(RuleSetFactory.class.getName()).getHandlers();
        for (Handler handler : handlers) {
            Logger.getLogger(RuleSetFactory.class.getName()).removeHandler(handler);
        }
    }

    @Test
    public void noDeprecations() throws RuleSetNotFoundException {
        Logger.getLogger(RuleSetFactory.class.getName()).addHandler(new Handler() {
            @Override
            public void publish(LogRecord record) {
                Assert.fail("No Logging expected: " + record.getMessage());
            }

            @Override
            public void flush() {
            }

            @Override
            public void close() throws SecurityException {
            }
        });

        RuleSetFactory ruleSetFactory = new RuleSetFactory(new ResourceLoader(), RulePriority.LOW, true, false);
        RuleSet quickstart = ruleSetFactory.createRuleSet("rulesets/java/quickstart.xml");
        Assert.assertFalse(quickstart.getRules().isEmpty());
    }
}
