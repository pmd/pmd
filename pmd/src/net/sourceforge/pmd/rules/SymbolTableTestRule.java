package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTExplicitConstructorInvocation;
import net.sourceforge.pmd.ast.ASTArguments;

public class SymbolTableTestRule extends AbstractRule implements Rule {

    public Object visit(ASTExplicitConstructorInvocation node, Object data) {
        System.out.println("ASTExplicitConstructorInvocation: arg count =  " + ((ASTArguments)node.jjtGetChild(0)).getArgumentCount());
        return super.visit(node,data);
    }

}
