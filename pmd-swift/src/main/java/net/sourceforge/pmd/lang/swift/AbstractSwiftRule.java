/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.AstVisitor;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrBaseRule;
import net.sourceforge.pmd.lang.swift.ast.SwiftInnerNode;

public abstract class AbstractSwiftRule extends AntlrBaseRule {

    protected AbstractSwiftRule() {
        // inheritance constructor
    }

    @Override
    public void addRuleChainVisit(Class<? extends Node> nodeClass) {
        // note that this is made unnecessary by #2490
        if (SwiftInnerNode.class.isAssignableFrom(nodeClass)) {
            addRuleChainVisit(nodeClass.getSimpleName().substring("Sw".length()));
            return;
        }
        super.addRuleChainVisit(nodeClass);
    }

    @Override
    public abstract AstVisitor<RuleContext, ?> buildVisitor();
}
