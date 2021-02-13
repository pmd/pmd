/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.contrib.java.lang.system.RestoreSystemProperties;
import org.junit.rules.TestRule;

import net.sourceforge.pmd.internal.SystemProps;
import net.sourceforge.pmd.lang.DummyLanguageModule;
import net.sourceforge.pmd.lang.Language;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.rule.AbstractRule;

public class SourceCodeProcessorTest {

    @org.junit.Rule
    public TestRule restoreSystemProperties = new RestoreSystemProperties();

    private SourceCodeProcessor processor;
    private StringReader sourceCode;
    private RuleContext ctx;
    private List<RuleSet> rulesets;
    private LanguageVersion dummyThrows;
    private LanguageVersion dummyDefault;

    @Before
    public void prepare() {
        Language dummyLanguage = LanguageRegistry.findLanguageByTerseName(DummyLanguageModule.TERSE_NAME);
        dummyDefault = dummyLanguage.getDefaultVersion();
        dummyThrows = dummyLanguage.getVersion("1.9-throws");

        processor = new SourceCodeProcessor(new PMDConfiguration());
        sourceCode = new StringReader("test");
        Rule rule = new RuleThatThrows();
        rulesets = Arrays.asList(RuleSet.forSingleRule(rule));

        ctx = new RuleContext();
    }

    @Test
    public void inErrorRecoveryModeErrorsShouldBeLoggedByParser() {
        System.setProperty(SystemProps.PMD_ERROR_RECOVERY, "");
        ctx.setLanguageVersion(dummyThrows);

        Assert.assertThrows(PMDException.class, () -> {
            processor.processSourceCode(sourceCode, new RuleSets(rulesets), ctx);
        });
        // the error is actually logged by PmdRunnable
    }

    @Test
    public void inErrorRecoveryModeErrorsShouldBeLoggedByRule() throws Exception {
        System.setProperty(SystemProps.PMD_ERROR_RECOVERY, "");
        ctx.setLanguageVersion(dummyDefault);

        processor.processSourceCode(sourceCode, new RuleSets(rulesets), ctx);
        Assert.assertEquals(1, ctx.getReport().getProcessingErrors().size());
        Assert.assertSame(AssertionError.class, ctx.getReport().getProcessingErrors().get(0).getError().getClass());
    }

    @Test
    public void withoutErrorRecoveryModeProcessingShouldBeAbortedByParser() {
        Assert.assertNull(System.getProperty(SystemProps.PMD_ERROR_RECOVERY));
        ctx.setLanguageVersion(dummyThrows);

        Assert.assertThrows(AssertionError.class, () -> {
            processor.processSourceCode(sourceCode, new RuleSets(rulesets), ctx);
        });
    }

    @Test
    public void withoutErrorRecoveryModeProcessingShouldBeAbortedByRule() {
        Assert.assertNull(System.getProperty(SystemProps.PMD_ERROR_RECOVERY));
        ctx.setLanguageVersion(dummyDefault);

        Assert.assertThrows(AssertionError.class, () -> {
            processor.processSourceCode(sourceCode, new RuleSets(rulesets), ctx);
        });
    }

    private static class RuleThatThrows extends AbstractRule {

        RuleThatThrows() {
            Language dummyLanguage = LanguageRegistry.findLanguageByTerseName(DummyLanguageModule.TERSE_NAME);
            setLanguage(dummyLanguage);
        }

        @Override
        public void apply(Node target, RuleContext ctx) {
            throw new AssertionError("test");
        }
    }
}
