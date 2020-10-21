/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import org.junit.After;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.SystemErrRule;

import net.sourceforge.pmd.RulePriority;
import net.sourceforge.pmd.RuleSet;
import net.sourceforge.pmd.RuleSetFactory;
import net.sourceforge.pmd.util.ResourceLoader;

public class DefaultRulesetTest {
    @Rule
    public final SystemErrRule systemErrRule = new SystemErrRule().enableLog().muteForSuccessfulTests();

    private RuleSetFactory factory = new RuleSetFactory(new ResourceLoader(), RulePriority.LOW, true, false);

    @Test
    public void loadDefaultRuleset() throws Exception {
        RuleSet ruleset = factory.createRuleSet("rulesets/apex/ruleset.xml");
        Assert.assertNotNull(ruleset);
    }

    @After
    public void cleanup() {
        Handler[] handlers = Logger.getLogger(RuleSetFactory.class.getName()).getHandlers();
        for (Handler handler : handlers) {
            Logger.getLogger(RuleSetFactory.class.getName()).removeHandler(handler);
        }
    }

    @Test
    public void loadQuickstartRuleset() throws Exception {
        List<String> logRecords = new ArrayList<>();
        Logger.getLogger(RuleSetFactory.class.getName()).addHandler(new Handler() {
            @Override
            public void publish(LogRecord record) {
                logRecords.add(record.getMessage());
            }

            @Override
            public void flush() {
            }

            @Override
            public void close() throws SecurityException {
            }
        });
        RuleSet ruleset = factory.createRuleSet("rulesets/apex/quickstart.xml");
        Assert.assertNotNull(ruleset);

        // Expect 6 log records that correspond to the 3 deprecated rules.
        // Each deprecated rule has two log lines of the form
        // Discontinue using Rule name category/apex/performance.xml/AvoidSoqlInLoops as it is scheduled for removal from PMD. PMD 7.0.0 will remove support for this Rule.
        // Use Rule name category/apex/performance.xml/AvoidSoqlInLoops instead of the deprecated Rule name rulesets/apex/quickstart.xml/AvoidSoqlInLoops. PMD 7.0.0 will remove support for this deprecated Rule name usage.
        Assert.assertEquals(logRecords.toString(), 6, logRecords.size());
        Pattern expectedDeprecatedRuleMessages = Pattern.compile(".*(AvoidDmlStatementsInLoops|AvoidSoqlInLoops|AvoidSoslInLoops) (as it is scheduled for removal from PMD|instead of the deprecated Rule name).*");
        for (String logRecord : logRecords) {
            Assert.assertTrue(logRecord, expectedDeprecatedRuleMessages.matcher(logRecord).matches());
        }
    }
}
