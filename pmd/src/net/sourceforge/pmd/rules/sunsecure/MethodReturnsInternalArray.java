/*
 * Created on Jan 17, 2005 
 *
 * $Id$
 */
package net.sourceforge.pmd.rules.sunsecure;

import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.ast.ASTReturnStatement;
import net.sourceforge.pmd.ast.ASTTypeDeclaration;

import java.util.Iterator;
import java.util.List;

/**
 * Implementation note: this rule currently ignores return types of y.x.z, 
 * currently it handles only local type fields.
 * 
 * @author mgriffa
 */
public class MethodReturnsInternalArray extends AbstractSunSecureRule {

    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (node.isInterface()) {
            return data;
        }
        return super.visit(node, data);
    }

    public Object visit(ASTMethodDeclaration method, Object data) {
        if (!method.getResultType().returnsArray()) {
            return data;
        }
        List returns = method.findChildrenOfType(ASTReturnStatement.class);
        ASTTypeDeclaration td = (ASTTypeDeclaration) method.getFirstParentOfType(ASTTypeDeclaration.class);
        for (Iterator it = returns.iterator() ; it.hasNext() ; ) {
            final ASTReturnStatement ret = (ASTReturnStatement) it.next();
            final String vn = getReturnedVariableName(ret);
            if (!isField(vn, td)) {
                continue;
            }
            if (ret.findChildrenOfType(ASTPrimarySuffix.class).size() > 2) {
                continue;
            }
            if (!isLocalVariable(vn, method)) {
                addViolation(data, ret, vn);
            }  else {
                // This is to handle field hiding
                final ASTPrimaryPrefix pp = (ASTPrimaryPrefix) ret.getFirstChildOfType(ASTPrimaryPrefix.class);
                if (pp!=null && pp.usesThisModifier()) {
                    final ASTPrimarySuffix ps = (ASTPrimarySuffix) ret.getFirstChildOfType(ASTPrimarySuffix.class);
                    if (ps.getImage().equals(vn)) {
                        addViolation(data, ret, vn);
                    }
                }
            }
        }
        return data;
    }


}
