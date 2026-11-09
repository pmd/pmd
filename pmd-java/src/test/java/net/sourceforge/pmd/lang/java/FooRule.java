/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java;

import net.sourceforge.pmd.lang.java.ast.ASTClassDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableId;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.reporting.RuleContext;

public class FooRule extends AbstractJavaRule {

    public FooRule() {
        this("No Foo allowed");
    }

    public FooRule(String message) {
        setMessage(message);
    }

    @Override
    public Object visit(ASTClassDeclaration c, Object data) {
        RuleContext ctx = (RuleContext) data;
        if ("Foo".equalsIgnoreCase(c.getSimpleName())) {
            ctx.addViolation(c);
        }
        return super.visit(c, ctx);
    }

    @Override
    public Object visit(ASTVariableId c, Object data) {
        RuleContext ctx = (RuleContext) data;
        if ("Foo".equalsIgnoreCase(c.getName())) {
            ctx.addViolation(c);
        }
        return super.visit(c, ctx);
    }

    @Override
    public String getName() {
        return "NoFoo";
    }
}
