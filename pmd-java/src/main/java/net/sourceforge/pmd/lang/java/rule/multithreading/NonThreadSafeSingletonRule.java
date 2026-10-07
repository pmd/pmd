/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.multithreading;

import static net.sourceforge.pmd.properties.PropertyFactory.booleanProperty;
import static net.sourceforge.pmd.properties.internal.PropertyParsingUtil.DEPRECATED_RULE_PROPERTY_MARKER;

import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.ast.ASTAssignmentExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTNullLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTSynchronizedStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableId;
import net.sourceforge.pmd.lang.java.ast.JModifier;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.reporting.RuleContext;

public class NonThreadSafeSingletonRule extends AbstractJavaRulechainRule {


    private static final PropertyDescriptor<Boolean> CHECK_NON_STATIC_METHODS_DESCRIPTOR = booleanProperty(
            "checkNonStaticMethods")
                    .desc(DEPRECATED_RULE_PROPERTY_MARKER + "This property is ignored and has no effect - non-static methods are "
                            + "always checked now (see https://github.com/pmd/pmd/issues/6780). This property "
                            + "will be removed in PMD 8.0.0.")
                    .defaultValue(true).build();
    private static final PropertyDescriptor<Boolean> CHECK_NON_STATIC_FIELDS_DESCRIPTOR = booleanProperty(
            "checkNonStaticFields")
                    .desc("Check only static fields (false), or check additionally for non-static fields (true).")
                    .defaultValue(false).build();

    private boolean checkNonStaticFields = true;

    public NonThreadSafeSingletonRule() {
        super(ASTMethodDeclaration.class);
        definePropertyDescriptor(CHECK_NON_STATIC_METHODS_DESCRIPTOR);
        definePropertyDescriptor(CHECK_NON_STATIC_FIELDS_DESCRIPTOR);
    }


    @Override
    public void start(RuleContext ctx) {
        checkNonStaticFields = getProperty(CHECK_NON_STATIC_FIELDS_DESCRIPTOR);
    }


    private boolean shouldConsiderField(@Nullable ASTVariableId varId) {
        if (varId == null) {
            return false;
        }

        ASTFieldDeclaration fieldDeclaration = varId.ancestors(ASTFieldDeclaration.class).firstOrThrow();
        return checkNonStaticFields || fieldDeclaration.hasModifiers(JModifier.STATIC);
    }


    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        if (node.hasModifiers(JModifier.SYNCHRONIZED)) {
            return data;
        }

        List<ASTIfStatement> ifStatements = node.descendants(ASTIfStatement.class).toList();
        for (ASTIfStatement ifStatement : ifStatements) {
            if (!isNullCheckOnField(ifStatement.getCondition(), null)) {
                continue;
            }
            List<ASTAssignmentExpression> assignments = ifStatement.descendants(ASTAssignmentExpression.class).toList();
            boolean violation = false;
            for (ASTAssignmentExpression assignment : assignments) {
                if (fieldWriteName(assignment) != null) {
                    violation = true;
                }
            }
            if (violation) {
                asCtx(data).addViolation(ifStatement);
            }
        }

        // The same check-then-act race can be written as a ternary assignment:
        //   field = field == null ? new T() : field;
        // which is not an ASTIfStatement and was previously missed.
        for (ASTAssignmentExpression assignment : node.descendants(ASTAssignmentExpression.class).toList()) {
            ASTVariableId fieldVarId = fieldWriteName(assignment);
            if (fieldVarId == null) {
                continue;
            }
            if (!(assignment.getRightOperand() instanceof ASTConditionalExpression)) {
                continue;
            }
            ASTConditionalExpression ternary = (ASTConditionalExpression) assignment.getRightOperand();
            if (!isNullCheckOnField(ternary.getCondition(), fieldVarId)) {
                continue;
            }
            asCtx(data).addViolation(assignment);
        }
        return data;
    }


    /**
     * Returns whether {@code condition} is a null-check on a considered singleton field.
     * If {@code targetField} is non-null, the checked field must equal it (used by the
     * ternary case, where the same field is both read in the condition and written);
     * otherwise any considered field matches (used by the if case, which keeps pmd's
     * long-standing conservative behavior).
     */
    private boolean isNullCheckOnField(ASTExpression condition, ASTVariableId targetField) {
        if (condition.descendants(ASTNullLiteral.class).isEmpty()) {
            return false;
        }
        ASTNamedReferenceExpr ref = condition.descendants(ASTNamedReferenceExpr.class).first();

        if (ref != null) {
            JVariableSymbol symbol = ref.getReferencedSym();
            if (symbol instanceof JFieldSymbol) {
                ASTVariableId fieldVarId = symbol.tryGetNode();
                return targetField == null ? shouldConsiderField(fieldVarId) : targetField.equals(fieldVarId);
            }
        }
        return false;
    }


    /**
     * Returns the variable id of the field written by {@code assignment}, or {@code null}
     * if {@code assignment} is not a non-synchronized write to one of the tracked singleton
     * fields. Shared by the if-statement and ternary check-then-act detection so both cases
     * apply identical field-write criteria and cannot drift apart.
     */
    private ASTVariableId fieldWriteName(ASTAssignmentExpression assignment) {
        if (assignment.ancestors(ASTSynchronizedStatement.class).nonEmpty()) {
            return null;
        }
        ASTAssignableExpr left = assignment.getLeftOperand();
        if (!(left instanceof ASTNamedReferenceExpr)) {
            return null;
        }
        ASTNamedReferenceExpr ref = (ASTNamedReferenceExpr) left;
        JVariableSymbol symbol = ref.getReferencedSym();
        if (!(symbol instanceof JFieldSymbol)) {
            return null;
        }
        ASTVariableId variableId = symbol.tryGetNode();
        if (!shouldConsiderField(variableId)) {
            return null;
        }

        return variableId;
    }
}
