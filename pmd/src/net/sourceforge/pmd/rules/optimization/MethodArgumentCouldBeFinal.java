/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules.optimization;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTFormalParameter;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.symboltable.NameOccurrence;
import net.sourceforge.pmd.symboltable.Scope;
import net.sourceforge.pmd.symboltable.VariableNameDeclaration;

import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MethodArgumentCouldBeFinal extends AbstractOptimizationRule {

    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (node.isInterface()) {
            return data;
        }
        return super.visit(node, data);
    }

    public Object visit(ASTMethodDeclaration meth, Object data) {
        if (meth.isNative() || meth.isAbstract()) {
            return data;
        }
        Scope s = meth.getScope();
        Map decls = s.getVariableDeclarations();
        for (Iterator i = decls.keySet().iterator(); i.hasNext();) {
            VariableNameDeclaration var = (VariableNameDeclaration)i.next();
            if (!var.getAccessNodeParent().isFinal() && (var.getAccessNodeParent() instanceof ASTFormalParameter) && !assigned((List)decls.get(var))) {
                addViolation(data, var.getAccessNodeParent(), var.getImage());
            }
        }
        return data;
    }

    private boolean assigned(List usages) {
        for (Iterator j = usages.iterator(); j.hasNext();) {
            NameOccurrence occ = (NameOccurrence) j.next();
            if (occ.isOnLeftHandSide() || occ.isSelfAssignment()) {
                return true;
            } 
            continue;
        }
        return false;
    }
}
