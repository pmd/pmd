/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules.optimization;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;

public class LocalVariableCouldBeFinal extends AbstractOptimizationRule {

    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (node.isInterface()) {
            return data;
        }
        return super.visit(node, data);
    }

    /**
     * Find if this variable is ever written.
     * 
     * @see net.sourceforge.pmd.ast.JavaParserVisitor#visit(net.sourceforge.pmd.ast.ASTLocalVariableDeclaration, java.lang.Object)
     */
    public Object visit(ASTLocalVariableDeclaration node, Object data) {
        //ignore if already declared final
        if (node.isFinal()) {
            return data;
        }
        final String varName = getVarName(node); 
        
        ASTMethodDeclaration md = (ASTMethodDeclaration) node.getFirstParentOfType(ASTMethodDeclaration.class);
        if (md!=null) {
            if (!isVarWritterInMethod(varName, md) ) {
                addViolation((RuleContext)data, node);
            }
        } 
        return data;
    }

}
