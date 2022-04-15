/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import java.util.function.Predicate;

import org.junit.Before;
import org.junit.contrib.java.lang.system.SystemErrRule;
import org.mockito.Mockito;
import org.mockito.verification.VerificationMode;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;

import net.sourceforge.pmd.junit.LocaleRule;
import net.sourceforge.pmd.util.log.MessageReporter;
import net.sourceforge.pmd.util.log.internal.SimpleMessageReporter;

public class RulesetFactoryTestBase {

    @org.junit.Rule
    public LocaleRule localeRule = LocaleRule.en();

    @org.junit.Rule
    public SystemErrRule systemErrRule = new SystemErrRule().muteForSuccessfulTests();

    protected MessageReporter mockReporter;

    @Before
    public void setup() {
        SimpleMessageReporter reporter = new SimpleMessageReporter(LoggerFactory.getLogger(RulesetFactoryTestBase.class));
        mockReporter = Mockito.spy(reporter);
    }

    protected void verifyNoWarnings() {
        Mockito.verifyZeroInteractions(mockReporter);
    }

    protected static Predicate<String> containing(String part) {
        return it -> it.contains(part);
    }

    protected void verifyFoundAWarningWithMessage(Predicate<String> messageTest) {
        verifyFoundWarningWithMessage(Mockito.times(1), messageTest);
    }

    protected void verifyFoundWarningWithMessage(VerificationMode mode, Predicate<String> messageTest) {
        Mockito.verify(mockReporter, mode)
               .logEx(Mockito.eq(Level.WARN), Mockito.argThat(messageTest::test), Mockito.any(), Mockito.any());
    }

    protected void verifyFoundAnErrorWithMessage(Predicate<String> messageTest) {
        Mockito.verify(mockReporter, Mockito.times(1))
               .logEx(Mockito.eq(Level.ERROR), Mockito.argThat(messageTest::test), Mockito.any(), Mockito.any());
    }


    protected RuleSet loadRuleSetInDir(String resourceDir, String ruleSetFilename) {
        RuleSetLoader loader = new RuleSetLoader();
        loader.setReporter(mockReporter);
        return loader.loadFromResource(resourceDir + "/" + ruleSetFilename);
    }

}
