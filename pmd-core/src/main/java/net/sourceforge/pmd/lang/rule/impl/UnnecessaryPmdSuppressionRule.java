/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.impl;

import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.rule.AbstractRule;
import net.sourceforge.pmd.lang.rule.internal.RuleSets;
import net.sourceforge.pmd.reporting.InternalApiBridge;
import net.sourceforge.pmd.reporting.RuleContext;
import net.sourceforge.pmd.reporting.ViolationSuppressor;
import net.sourceforge.pmd.reporting.ViolationSuppressor.UnusedSuppressorNode;
import net.sourceforge.pmd.util.CollectionUtil;

/**
 * A rule that reports unused suppression annotations and comments.
 * This rule class supports any PMD language, but language-specific behavior
 * needs to be implemented to avoid false positives for some languages.
 * Violations of this rule cannot be suppressed. It is special cased
 * by {@link RuleSets} to execute after all other rules, so that whether
 * those produce warnings or not is known to this rule.
 *
 * @experimental Since 7.14.0. See <a href="https://github.com/pmd/pmd/pull/5609">[core] Add rule to report unnecessary suppression comments/annotations #5609</a>
 */
@Experimental
public class UnnecessaryPmdSuppressionRule extends AbstractRule implements CannotBeSuppressed {

    @Override
    public void apply(Node rootNode, RuleContext ctx) {
        assert rootNode instanceof RootNode;

        LanguageVersionHandler handler = rootNode.getAstInfo().getLanguageProcessor().services();
        List<ViolationSuppressor> suppressors = CollectionUtil.concatView(
            handler.getExtraViolationSuppressors(),
            InternalApiBridge.DEFAULT_SUPPRESSORS
        );

        for (ViolationSuppressor suppressor : suppressors) {
            Set<UnusedSuppressorNode> unusedSuppressors = suppressor.getUnusedSuppressors((RootNode) rootNode);
            for (UnusedSuppressorNode unusedSuppressor : unusedSuppressors) {
                ctx.at(unusedSuppressor.getLocation()).warnWithMessage(unusedSuppressor.unusedReason());
            }
        }
    }

}
