/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule;

import java.lang.reflect.Modifier;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.java.ast.JavaNode;

/**
 * Base class for rules using the rulechain. The visit methods don't
 * recurse by default.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
public abstract class AbstractJavaRulechainRule extends AbstractJavaRule {

    /**
     * Specify the node types to visit as parameters.
     *
     * @param first  The first node, there must be at least one
     * @param visits The rest
     */
    @SafeVarargs
    @Experimental
    public AbstractJavaRulechainRule(Class<? extends JavaNode> first, Class<? extends JavaNode>... visits) {
        addRuleChainVisit(first);
        for (Class<? extends JavaNode> visit : visits) {
            addRuleChainVisit(visit);
        }
    }


    @Override
    public Object visit(JavaNode node, Object data) {
        return data;
    }
}
