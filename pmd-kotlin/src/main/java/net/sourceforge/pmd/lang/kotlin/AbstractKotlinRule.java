/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin;

import net.sourceforge.pmd.lang.kotlin.ast.KotlinVisitor;
import net.sourceforge.pmd.lang.rule.AbstractVisitorRule;
import net.sourceforge.pmd.reporting.RuleContext;

public abstract class AbstractKotlinRule extends AbstractVisitorRule {

    protected AbstractKotlinRule() {
        // inheritance constructor
    }

    @Override
    public abstract KotlinVisitor<RuleContext, ?> buildVisitor();
}
