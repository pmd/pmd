package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.ast.ASTExplicitConstructorInvocation;

public class SymbolTableTestRule extends AbstractRule implements Rule {

    public Object visit(ASTExplicitConstructorInvocation node, Object data) {
        System.out.println("ASTExplicitConstructorInvocation: arg count =  " + node.getArgumentCount());
        return super.visit(node,data);
    }

}
