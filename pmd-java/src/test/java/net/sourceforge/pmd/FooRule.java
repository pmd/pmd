/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class FooRule extends AbstractJavaRule {

    public FooRule() {
        setMessage("No Foo allowed");
    }

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration c, Object ctx) {
        if (c.getImage().equalsIgnoreCase("Foo")) {
            addViolation(ctx, c);
        }
        return super.visit(c, ctx);
    }

    @Override
    public Object visit(ASTVariableDeclaratorId c, Object ctx) {
        if (c.getImage().equalsIgnoreCase("Foo")) {
            addViolation(ctx, c);
        }
        return super.visit(c, ctx);
    }

    @Override
    public String getName() {
        return "NoFoo";
    }
}
