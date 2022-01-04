/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript;

import org.junit.Assert;
import org.junit.Test;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.RuleViolation;
import net.sourceforge.pmd.lang.LanguageRegistry;
import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.ecmascript.ast.ASTAstRoot;
import net.sourceforge.pmd.lang.ecmascript.ast.JsParsingHelper;
import net.sourceforge.pmd.lang.ecmascript.rule.AbstractEcmascriptRule;
import net.sourceforge.pmd.lang.rule.RuleViolationFactory;

public class EcmasccriptLanguageModuleTest {
    private Rule rule = new AbstractEcmascriptRule() { };
    private ASTAstRoot node = JsParsingHelper.DEFAULT.parse("function a() {}");
    private LanguageVersion js = LanguageRegistry.getLanguage(EcmascriptLanguageModule.NAME).getDefaultVersion();
    private LanguageVersionHandler languageVersionHandler = js.getLanguageVersionHandler();
    private RuleViolationFactory ruleViolationFactory = languageVersionHandler.getRuleViolationFactory();

    @Test
    public void canCreateRuleViolation() {
        RuleContext context = new RuleContext();
        ruleViolationFactory.addViolation(context, rule, node, "the message", new Object[0]);
        Assert.assertEquals(1, context.getReport().getViolations().size());
        RuleViolation ruleViolation = context.getReport().getViolations().get(0);
        Assert.assertEquals(1, ruleViolation.getBeginLine());
    }

    @Test
    public void canCreateRuleViolationWithLineNumbers() {
        RuleContext context = new RuleContext();
        ruleViolationFactory.addViolation(context, rule, node, "the message", 5, 7, new Object[0]);
        Assert.assertEquals(1, context.getReport().getViolations().size());
        RuleViolation ruleViolation = context.getReport().getViolations().get(0);
        Assert.assertEquals(5, ruleViolation.getBeginLine());
        Assert.assertEquals(7, ruleViolation.getEndLine());
    }
}
