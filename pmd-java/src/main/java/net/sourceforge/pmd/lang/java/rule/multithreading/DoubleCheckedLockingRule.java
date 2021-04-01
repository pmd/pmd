/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.multithreading;

import java.lang.reflect.Modifier;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.ast.ASTAssignmentExpression;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTInfixExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTNullLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTPrimitiveType;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTSynchronizedStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.BinaryOp;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol;
import net.sourceforge.pmd.lang.java.symbols.JLocalVariableSymbol;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;

/**
 * <pre>
 * void method() {
 *   if (x == null) {
 *     synchronized(this){
 *       if (x == null) {
 *         x = new | method();
 *       }
 *     }
 *   }
 * }
 * </pre>
 *
 * <p>The error is when one uses the value assigned within a synchronized
 * section, outside of a synchronized section.</p>
 *
 * <pre>
 * if (x == null) // is outside of synchronized section
 *   x = new | method();
 * </pre>
 *
 * <p>Very very specific check for double checked locking.</p>
 *
 * @author CL Gilbert (dnoyeb@users.sourceforge.net)
 */
public class DoubleCheckedLockingRule extends AbstractJavaRule {

    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(ASTMethodDeclaration.class);
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        if (node.isVoid() || node.getResultTypeNode() instanceof ASTPrimitiveType || node.getBody() == null) {
            return data;
        }

        List<ASTReturnStatement> rsl = node.descendants(ASTReturnStatement.class).toList();
        if (rsl.size() != 1) {
            return data;
        }
        ASTReturnStatement rs = rsl.get(0);

        ASTExpression returnExpr = rs.getExpr();
        if (!(returnExpr instanceof ASTNamedReferenceExpr)) {
            return data;
        }

        JVariableSymbol returnVariable = ((ASTNamedReferenceExpr) returnExpr).getReferencedSym();
        // With Java5 and volatile keyword, DCL is no longer an issue
        if (returnVariable instanceof JFieldSymbol
            && Modifier.isVolatile(((JFieldSymbol) returnVariable).getModifiers())) {
            return data;
        }

        // if the return variable is local and only written with the volatile
        // field, then it's ok, too
        if (isLocalOnlyStoredWithVolatileField(node, returnVariable)) {
            return super.visit(node, data);
        }

        List<ASTIfStatement> isl = node.findDescendantsOfType(ASTIfStatement.class);
        if (isl.size() == 2) {
            ASTIfStatement outerIf = isl.get(0);
            if (isNullCheck(outerIf.getCondition(), returnVariable)) {
                // find synchronized
                List<ASTSynchronizedStatement> ssl = outerIf.findDescendantsOfType(ASTSynchronizedStatement.class);
                if (ssl.size() == 1 && ssl.get(0).ancestors().any(it -> it == outerIf)) {
                    ASTIfStatement is2 = isl.get(1);
                    if (isNullCheck(is2.getCondition(), returnVariable)) {
                        List<ASTAssignmentExpression> assignments = is2.findDescendantsOfType(ASTAssignmentExpression.class);
                        if (assignments.size() == 1
                            && isReferenceTo(assignments.get(0).getLeftOperand(), returnVariable)) {
                            addViolation(data, node);

                        }
                    }
                }
            }
        }
        return super.visit(node, data);
    }

    private boolean isLocalOnlyStoredWithVolatileField(ASTMethodDeclaration method, JVariableSymbol local) {
        ASTExpression initializer;
        if (local instanceof JLocalVariableSymbol) {
            ASTVariableDeclaratorId id = local.tryGetNode();
            if (id == null) {
                return false;
            }
            initializer = id.getInitializer();
        } else {
            // the return variable name doesn't seem to be a local variable
            return false;
        }

        return (initializer == null || isVolatileFieldReference(initializer))
            && method.descendants(ASTAssignmentExpression.class)
                     .filter(it -> isReferenceTo(it.getLeftOperand(), local))
                     .all(it -> isVolatileFieldReference(it.getRightOperand()));
    }

    private boolean isVolatileFieldReference(@Nullable ASTExpression initializer) {
        if (initializer instanceof ASTNamedReferenceExpr) {
            JVariableSymbol fieldSym = ((ASTNamedReferenceExpr) initializer).getReferencedSym();
            return fieldSym instanceof JFieldSymbol && Modifier.isVolatile(((JFieldSymbol) fieldSym).getModifiers());
        } else {
            return false;
        }
    }

    private boolean isReferenceTo(@Nullable ASTExpression expr, JVariableSymbol symbol) {
        if (expr instanceof ASTNamedReferenceExpr) {
            return symbol != null && symbol.equals(((ASTNamedReferenceExpr) expr).getReferencedSym());
        } else {
            return false;
        }
    }

    private boolean isNullCheck(ASTExpression expr, JVariableSymbol var) {
        if (expr instanceof ASTInfixExpression) {
            ASTInfixExpression condition = (ASTInfixExpression) expr;
            if (condition.getOperator().hasSamePrecedenceAs(BinaryOp.EQ)) {
                ASTNullLiteral nullLit = condition.getFirstChildOfType(ASTNullLiteral.class);
                if (nullLit != null) {
                    ASTExpression otherChild = (ASTExpression) condition.getChild(1 - nullLit.getIndexInParent());
                    return isReferenceTo(otherChild, var);
                }
            }
        }
        return false;
    }

}
