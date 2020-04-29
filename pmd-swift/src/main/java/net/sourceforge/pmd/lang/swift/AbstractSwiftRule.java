/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrBaseRule;
import net.sourceforge.pmd.lang.swift.ast.SwiftTreeParser;
import net.sourceforge.pmd.lang.swift.ast.SwiftVisitor;

public abstract class AbstractSwiftRule extends AntlrBaseRule {

    protected AbstractSwiftRule() {
        // inheritance constructor
    }

    protected void addRuleChainVisit(int ruleIndex) {
        addRuleChainVisit(SwiftTreeParser.DICO.getXPathNameOfRule(ruleIndex));
    }

    @Override
    public abstract SwiftVisitor<RuleContext, Void> buildVisitor();
}
