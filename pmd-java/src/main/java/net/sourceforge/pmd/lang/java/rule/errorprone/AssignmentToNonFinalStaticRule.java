/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.AccessType;
import net.sourceforge.pmd.lang.java.ast.ASTFieldAccess;
import net.sourceforge.pmd.lang.java.ast.ASTVariableAccess;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;

/**
 * @author Eric Olander
 * @since Created on October 24, 2004, 8:56 AM
 */
public class AssignmentToNonFinalStaticRule extends AbstractJavaRulechainRule {

    public AssignmentToNonFinalStaticRule() {
        super(ASTFieldAccess.class, ASTVariableAccess.class);
    }

    @Override
    public Object visit(ASTVariableAccess node, Object data) {
        checkAccess(node, data);
        return null;
    }

    @Override
    public Object visit(ASTFieldAccess node, Object data) {
        checkAccess(node, data);
        return null;
    }

    private void checkAccess(ASTNamedReferenceExpr node, Object data) {
        if (node.getAccessType() == AccessType.WRITE) {
            @Nullable
            JVariableSymbol symbol = node.getReferencedSym();
            if (symbol != null && symbol.isField()) {
                JFieldSymbol field = (JFieldSymbol) symbol;
                if (field.isStatic() && !field.isFinal()) {
                    addViolation(data, node);
                }
            }
        }
    }
}
