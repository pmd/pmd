/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.multithreading;

import static net.sourceforge.pmd.properties.PropertyFactory.booleanProperty;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
                    .desc("Check for non-static fields.")
                    .defaultValue(false).build();

    private Set<String> fields = new HashSet<>();

    private boolean checkNonStaticFields = true;

    public NonThreadSafeSingletonRule() {
        super(ASTFieldDeclaration.class, ASTMethodDeclaration.class);
        definePropertyDescriptor(CHECK_NON_STATIC_METHODS_DESCRIPTOR);
        definePropertyDescriptor(CHECK_NON_STATIC_FIELDS_DESCRIPTOR);
    }


    @Override
    public void start(RuleContext ctx) {
        fields.clear();
        checkNonStaticFields = getProperty(CHECK_NON_STATIC_FIELDS_DESCRIPTOR);
    }


    @Override
    public Object visit(ASTFieldDeclaration node, Object data) {
        if (checkNonStaticFields || node.hasModifiers(JModifier.STATIC)) {
            for (ASTVariableId varId : node.getVarIds()) {
                fields.add(varId.getName());
            }
        }
        return data;
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
            String fieldName = fieldWriteName(assignment);
            if (fieldName == null) {
                continue;
            }
            if (!(assignment.getRightOperand() instanceof ASTConditionalExpression)) {
                continue;
            }
            ASTConditionalExpression ternary = (ASTConditionalExpression) assignment.getRightOperand();
            if (!isNullCheckOnField(ternary.getCondition(), fieldName)) {
                continue;
            }
            asCtx(data).addViolation(assignment);
        }
        return data;
    }


    /**
     * Returns whether {@code condition} is a null-check on a tracked singleton field.
     * If {@code targetField} is non-null, the checked field must equal it (used by the
     * ternary case, where the same field is both read in the condition and written);
     * otherwise any tracked field matches (used by the if case, which keeps pmd's
     * long-standing conservative behavior). Centralizing this lets the if and ternary
     * cases share identical condition-check logic.
     */
    private boolean isNullCheckOnField(ASTExpression condition, String targetField) {
        if (condition.descendants(ASTNullLiteral.class).isEmpty()) {
            return false;
        }
        ASTNamedReferenceExpr ref = condition.descendants(ASTNamedReferenceExpr.class).first();
        return ref != null
                && (targetField != null ? targetField.equals(ref.getName()) : fields.contains(ref.getName()));
    }


    /**
     * Returns the name of the monitored field written by {@code assignment}, or {@code null}
     * if {@code assignment} is not a non-synchronized write to one of the tracked singleton
     * fields. Shared by the if-statement and ternary check-then-act detection so both cases
     * apply identical field-write criteria and cannot drift apart.
     */
    private String fieldWriteName(ASTAssignmentExpression assignment) {
        if (assignment.ancestors(ASTSynchronizedStatement.class).nonEmpty()) {
            return null;
        }
        ASTAssignableExpr left = assignment.getLeftOperand();
        if (!(left instanceof ASTNamedReferenceExpr)) {
            return null;
        }
        ASTNamedReferenceExpr ref = (ASTNamedReferenceExpr) left;
        if (!(ref.getReferencedSym() instanceof JFieldSymbol)) {
            return null;
        }
        String name = ref.getName();
        return fields.contains(name) ? name : null;
    }
}
