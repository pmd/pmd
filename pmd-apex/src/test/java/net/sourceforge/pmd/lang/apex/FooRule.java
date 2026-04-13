/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex;

import net.sourceforge.pmd.lang.apex.ast.ASTField;
import net.sourceforge.pmd.lang.apex.ast.ASTParameter;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ASTVariableDeclaration;
import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.reporting.RuleContext;

/**
 * Sample rule that detect any node with an image of "Foo". Used for testing.
 */
public class FooRule extends AbstractApexRule {

    public FooRule() {
        setMessage("No Foo allowed");
    }

    @Override
    public RuleContext visit(ASTUserClass c, RuleContext ctx) {
        if ("Foo".equalsIgnoreCase(c.getSimpleName())) {
            ctx.addViolation(c);
        }
        return super.visit(c, ctx);
    }

    @Override
    public RuleContext visit(ASTVariableDeclaration c, RuleContext ctx) {
        if ("Foo".equalsIgnoreCase(c.getImage())) {
            ctx.addViolation(c);
        }
        return super.visit(c, ctx);
    }

    @Override
    public RuleContext visit(ASTField c, RuleContext ctx) {
        if ("Foo".equalsIgnoreCase(c.getImage())) {
            ctx.addViolation(c);
        }
        return super.visit(c, ctx);
    }

    @Override
    public RuleContext visit(ASTParameter c, RuleContext ctx) {
        if ("Foo".equalsIgnoreCase(c.getImage())) {
            ctx.addViolation(c);
        }
        return super.visit(c, ctx);
    }

    @Override
    public String getName() {
        return "NoFoo";
    }
}
