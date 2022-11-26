/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrBaseRule;
import net.sourceforge.pmd.lang.kotlin.ast.KotlinVisitor;

public abstract class AbstractKotlinRule extends AntlrBaseRule {

    protected AbstractKotlinRule() {
        // inheritance constructor
    }

    @Override
    public abstract KotlinVisitor<RuleContext, ?> buildVisitor();
}
