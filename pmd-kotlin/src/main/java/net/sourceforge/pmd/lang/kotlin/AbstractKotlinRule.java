/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.AstVisitor;
import net.sourceforge.pmd.lang.ast.impl.antlr4.AntlrBaseRule;

public abstract class AbstractKotlinRule extends AntlrBaseRule {

    protected AbstractKotlinRule() {
        // inheritance constructor
    }

    @Override
    public abstract AstVisitor<RuleContext, ?> buildVisitor();
}
