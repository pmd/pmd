/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.swift;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.rule.AbstractVisitorRule;
import net.sourceforge.pmd.lang.swift.ast.SwiftVisitor;

public abstract class AbstractSwiftRule extends AbstractVisitorRule {

    protected AbstractSwiftRule() {
        // inheritance constructor
    }

    @Override
    public abstract SwiftVisitor<RuleContext, ?> buildVisitor();
}
