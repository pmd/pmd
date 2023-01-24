/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class FooRule extends AbstractJavaRule {

    public FooRule() {
        this("No Foo allowed");
    }

    public FooRule(String message) {
        setMessage(message);
    }

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration c, Object ctx) {
        if (c.getSimpleName().equalsIgnoreCase("Foo")) {
            addViolation(ctx, c);
        }
        return super.visit(c, ctx);
    }

    @Override
    public Object visit(ASTVariableDeclaratorId c, Object ctx) {
        if (c.getName().equalsIgnoreCase("Foo")) {
            addViolation(ctx, c);
        }
        return super.visit(c, ctx);
    }

    @Override
    public String getName() {
        return "NoFoo";
    }
}
