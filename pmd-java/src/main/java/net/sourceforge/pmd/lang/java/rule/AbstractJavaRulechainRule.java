/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule;

import java.util.Set;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;
import net.sourceforge.pmd.util.CollectionUtil;

/**
 * Base class for rules using the rulechain. The visit methods don't
 * recurse by default.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public abstract class AbstractJavaRulechainRule extends AbstractJavaRule {

    private final Set<Class<? extends Node>> typesToVisit;

    /**
     * Specify the node types to visit as parameters.
     *
     * @param first  The first node, there must be at least one
     * @param visits The rest
     */
    @SafeVarargs
    @Experimental
    public AbstractJavaRulechainRule(Class<? extends JavaNode> first, Class<? extends JavaNode>... visits) {
        typesToVisit = CollectionUtil.setOf(first, visits);
    }

    @Override
    protected final @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(typesToVisit);
    }

    @Override
    public Object visit(JavaNode node, Object data) {
        return data;
    }
}
