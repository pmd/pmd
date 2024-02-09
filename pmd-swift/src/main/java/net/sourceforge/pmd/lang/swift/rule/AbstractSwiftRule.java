/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift.rule;

import net.sourceforge.pmd.lang.rule.AbstractVisitorRule;
import net.sourceforge.pmd.lang.swift.ast.SwiftVisitor;
import net.sourceforge.pmd.reporting.RuleContext;

public abstract class AbstractSwiftRule extends AbstractVisitorRule {

    protected AbstractSwiftRule() {
        // inheritance constructor
    }

    @Override
    public abstract SwiftVisitor<RuleContext, ?> buildVisitor();
}
