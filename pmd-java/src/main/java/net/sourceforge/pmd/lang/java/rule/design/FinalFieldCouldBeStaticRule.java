/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import net.sourceforge.pmd.lang.java.ast.ASTArrayAllocation;
import net.sourceforge.pmd.lang.java.ast.ASTArrayDimExpr;
import net.sourceforge.pmd.lang.java.ast.ASTArrayInitializer;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.ast.ASTBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFieldAccess;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTInfixExpression;
import net.sourceforge.pmd.lang.java.ast.ASTInitializer;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTSynchronizedStatement;
import net.sourceforge.pmd.lang.java.ast.ASTTypeExpression;
import net.sourceforge.pmd.lang.java.ast.ASTVariableAccess;
import net.sourceforge.pmd.lang.java.ast.ASTVariableId;
import net.sourceforge.pmd.lang.java.ast.JModifier;
import net.sourceforge.pmd.lang.java.ast.ModifierOwner;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;

public class FinalFieldCouldBeStaticRule extends AbstractJavaRulechainRule {

    public FinalFieldCouldBeStaticRule() {
        super(ASTFieldDeclaration.class);
    }

    @Override
    public Object visit(ASTFieldDeclaration node, Object data) {
        if (node.hasModifiers(JModifier.FINAL) && !node.isStatic()
                && !node.getEnclosingType().isAnnotationPresent("lombok.experimental.UtilityClass")
                && !node.isAnnotationPresent("lombok.Builder.Default")) {

            for (ASTVariableId field : node) {
                ASTExpression init = field.getInitializer();
                if (init != null && isAllowedExpression(init) && !isUsedForSynchronization(field)) {
                    asCtx(data).addViolation(field);
                }
            }

        }
        return null;
    }

    private boolean isAllowedExpression(ASTExpression e) {
        if (e instanceof ASTLiteral || e instanceof ASTClassLiteral || e instanceof ASTTypeExpression
                || e.isCompileTimeConstant()) {
            return true;
        } else if (e instanceof ASTFieldAccess && "length".equals(((ASTFieldAccess) e).getName())
                && ((ASTFieldAccess) e).getQualifier().getTypeMirror().isArray()) {
            JVariableSymbol arrayDeclarationSymbol = ((ASTVariableAccess) ((ASTFieldAccess) e).getQualifier())
                    .getReferencedSym();
            if (arrayDeclarationSymbol != null && arrayDeclarationSymbol.isField()
                    && ((JFieldSymbol) arrayDeclarationSymbol).isStatic()
                    && arrayDeclarationSymbol.tryGetNode() != null) {
                ASTVariableId arrayVarId = arrayDeclarationSymbol.tryGetNode();
                return arrayVarId != null && arrayVarId.getInitializer() != null
                        && arrayVarId.getInitializer().isCompileTimeConstant();
            }
        } else if (e instanceof ASTNamedReferenceExpr) {
            JVariableSymbol sym = ((ASTNamedReferenceExpr) e).getReferencedSym();
            return sym != null && sym.isField() && ((JFieldSymbol) sym).isStatic();
        } else if (e instanceof ASTArrayAllocation) {
            ASTArrayInitializer init = ((ASTArrayAllocation) e).getArrayInitializer();
            if (init != null) {
                return init.length() == 0;
            }
            return ((ASTArrayAllocation) e).getTypeNode().getDimensions().toStream().filterIs(ASTArrayDimExpr.class)
                    .all(it -> JavaAstUtils.isLiteralInt(it.getLengthExpression(), 0));
        } else if (e instanceof ASTInfixExpression) {
            return isAllowedExpression(((ASTInfixExpression) e).getLeftOperand())
                    && isAllowedExpression(((ASTInfixExpression) e).getRightOperand());
        }

        return false;
    }

    private boolean isUsedForSynchronization(ASTVariableId field) {
        return field.getLocalUsages().stream().anyMatch(it -> it.getParent() instanceof ASTSynchronizedStatement
                && it.ancestors(ASTBodyDeclaration.class).take(1).any(d -> !isStatic(d)));
    }

    private boolean isStatic(ASTBodyDeclaration decl) {
        if (decl instanceof ModifierOwner) {
            return ((ModifierOwner) decl).hasModifiers(JModifier.STATIC);
        } else if (decl instanceof ASTInitializer) {
            return ((ASTInitializer) decl).isStatic();
        }
        return false;
    }
}
