/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.sunsecure;

import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTAllocationExpression;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTTypeDeclaration;

/**
 * Implementation note: this rule currently ignores return types of y.x.z,
 * currently it handles only local type fields.
 * Created on Jan 17, 2005
 *
 * @author mgriffa
 */
public class MethodReturnsInternalArrayRule extends AbstractSunSecureRule {

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (node.isInterface()) {
            return data;
        }
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTMethodDeclaration method, Object data) {
        if (!method.getResultType().returnsArray()) {
            return data;
        }
        List<ASTReturnStatement> returns = method.findDescendantsOfType(ASTReturnStatement.class);
        ASTTypeDeclaration td = method.getFirstParentOfType(ASTTypeDeclaration.class);
        for (ASTReturnStatement ret: returns) {
            final String vn = getReturnedVariableName(ret);
            if (!isField(vn, td)) {
                continue;
            }
            if (ret.findDescendantsOfType(ASTPrimarySuffix.class).size() > 2) {
                continue;
            }
            if (ret.hasDescendantOfType(ASTAllocationExpression.class)) {
                continue;
            }
            if (!isLocalVariable(vn, method)) {
                addViolation(data, ret, vn);
            } else {
                // This is to handle field hiding
                final ASTPrimaryPrefix pp = ret.getFirstDescendantOfType(ASTPrimaryPrefix.class);
                if (pp != null && pp.usesThisModifier()) {
                    final ASTPrimarySuffix ps = ret.getFirstDescendantOfType(ASTPrimarySuffix.class);
                    if (ps.hasImageEqualTo(vn)) {
                        addViolation(data, ret, vn);
                    }
                }
            }
        }
        return data;
    }


}
