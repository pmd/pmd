package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTExplicitConstructorInvocation;

public class SymbolTableTestRule extends AbstractRule implements Rule {

    public Object visit(ASTExplicitConstructorInvocation node, Object data) {
        System.out.println("ASTExplicitConstructorInvocation: isSuper: " + node.isSuper());
        System.out.println("ASTExplicitConstructorInvocation: isThis: " + node.isThis());
        return super.visit(node,data);
    }

}
