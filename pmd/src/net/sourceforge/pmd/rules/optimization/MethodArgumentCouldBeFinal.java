/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules.optimization;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.ast.ASTFormalParameter;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.AccessNode;
import net.sourceforge.pmd.symboltable.Scope;
import net.sourceforge.pmd.symboltable.VariableNameDeclaration;

public class MethodArgumentCouldBeFinal extends AbstractOptimizationRule {

    public Object visit(ASTMethodDeclaration meth, Object data) {
        if (meth.isNative() || meth.isAbstract()) {
            return data;
        }
        Scope s = meth.getScope();
        Map decls = s.getVariableDeclarations();
        for (Iterator i = decls.entrySet().iterator(); i.hasNext();) {
            Map.Entry entry = (Map.Entry) i.next();
            VariableNameDeclaration var = (VariableNameDeclaration) entry.getKey();
            AccessNode node = var.getAccessNodeParent();
            if (!node.isFinal() && (node instanceof ASTFormalParameter) && !assigned((List) entry.getValue())) {
                addViolation(data, node, var.getImage());
            }
        }
        return data;
    }

}
