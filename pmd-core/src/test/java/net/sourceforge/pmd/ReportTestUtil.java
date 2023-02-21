/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import java.util.function.BiConsumer;

import net.sourceforge.pmd.lang.LanguageProcessorRegistry;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.document.TestMessageReporter;

public final class ReportTestUtil {
    private ReportTestUtil() {
        // utility
    }

    public static Report getReport(Rule rule, BiConsumer<Rule, RuleContext> sideEffects) {
        return Report.buildReport(listener -> sideEffects.accept(rule, RuleContext.create(listener, rule)));
    }

    public static Report getReportForRuleApply(Rule rule, Node node) {
        return getReport(rule, (r, ctx) -> {
            r.initialize(node.getAstInfo().getLanguageProcessor());
            r.apply(node, ctx);
        });
    }

    public static Report getReportForRuleSetApply(RuleSet ruleset, RootNode node) {
        return Report.buildReport(listener -> {
            RuleSets ruleSets = new RuleSets(ruleset);
            LanguageProcessorRegistry registry = LanguageProcessorRegistry.singleton(node.getAstInfo().getLanguageProcessor());
            ruleSets.initializeRules(registry, new TestMessageReporter());
            ruleSets.apply(node, listener);
        });
    }

}
