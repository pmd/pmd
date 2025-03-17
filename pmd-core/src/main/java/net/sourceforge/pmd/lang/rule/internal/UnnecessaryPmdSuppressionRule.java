/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.rule.internal;

import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.rule.AbstractRule;
import net.sourceforge.pmd.reporting.InternalApiBridge;
import net.sourceforge.pmd.reporting.RuleContext;
import net.sourceforge.pmd.reporting.ViolationSuppressor;
import net.sourceforge.pmd.reporting.ViolationSuppressor.UnusedSuppressorNode;
import net.sourceforge.pmd.util.CollectionUtil;

/**
 * A rule that reports unused suppression annotations and comments.
 */
@Experimental
public class UnnecessaryPmdSuppressionRule extends AbstractRule {
    // it is in this package because it uses privileged API of RuleContex


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
                ctx.addViolationNoSuppress(
                    unusedSuppressor.getLocation(),
                    rootNode.getAstInfo(),
                    unusedSuppressor.unusedReason()
                );
            }
        }
    }

}
