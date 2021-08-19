/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.multithreading;

import static net.sourceforge.pmd.properties.PropertyFactory.booleanProperty;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.ast.ASTAssignmentExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTNullLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTSynchronizedStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.JModifier;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.symbols.JFieldSymbol;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;
import net.sourceforge.pmd.properties.PropertyDescriptor;

public class NonThreadSafeSingletonRule extends AbstractJavaRulechainRule {


    private static final PropertyDescriptor<Boolean> CHECK_NON_STATIC_METHODS_DESCRIPTOR = booleanProperty(
            "checkNonStaticMethods")
                    .desc("Check for non-static methods.  Do not set this to false and checkNonStaticFields to true.")
                    .defaultValue(true).build();
    private static final PropertyDescriptor<Boolean> CHECK_NON_STATIC_FIELDS_DESCRIPTOR = booleanProperty(
            "checkNonStaticFields")
                    .desc("Check for non-static fields.  Do not set this to true and checkNonStaticMethods to false.")
                    .defaultValue(false).build();

    private Set<String> fields = new HashSet<>();

    private boolean checkNonStaticMethods = true;
    private boolean checkNonStaticFields = true;

    public NonThreadSafeSingletonRule() {
        super(ASTFieldDeclaration.class, ASTMethodDeclaration.class);
        definePropertyDescriptor(CHECK_NON_STATIC_METHODS_DESCRIPTOR);
        definePropertyDescriptor(CHECK_NON_STATIC_FIELDS_DESCRIPTOR);
    }


    @Override
    public void start(RuleContext ctx) {
        fields.clear();
        checkNonStaticMethods = getProperty(CHECK_NON_STATIC_METHODS_DESCRIPTOR);
        checkNonStaticFields = getProperty(CHECK_NON_STATIC_FIELDS_DESCRIPTOR);
    }


    @Override
    public Object visit(ASTFieldDeclaration node, Object data) {
        if (checkNonStaticFields || node.hasModifiers(JModifier.STATIC)) {
            for (ASTVariableDeclaratorId varId : node.getVarIds()) {
                fields.add(varId.getName());
            }
        }
        return data;
    }


    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        if (checkNonStaticMethods && !node.hasModifiers(JModifier.STATIC)
                || node.hasModifiers(JModifier.SYNCHRONIZED)) {
            return data;
        }

        List<ASTIfStatement> ifStatements = node.descendants(ASTIfStatement.class).toList();
        for (ASTIfStatement ifStatement : ifStatements) {
            if (ifStatement.getCondition().descendants(ASTNullLiteral.class).isEmpty()) {
                continue;
            }
            ASTNamedReferenceExpr n = ifStatement.getCondition().descendants(ASTNamedReferenceExpr.class).first();
            if (n == null || !fields.contains(n.getName())) {
                continue;
            }
            List<ASTAssignmentExpression> assignments = ifStatement.descendants(ASTAssignmentExpression.class).toList();
            boolean violation = false;
            for (ASTAssignmentExpression assignment : assignments) {
                if (assignment.ancestors(ASTSynchronizedStatement.class).nonEmpty()) {
                    continue;
                }

                ASTAssignableExpr left = assignment.getLeftOperand();
                if (left instanceof ASTNamedReferenceExpr) {
                    JVariableSymbol referencedSym = ((ASTNamedReferenceExpr) left).getReferencedSym();
                    if (referencedSym instanceof JFieldSymbol) {
                        String name = ((ASTNamedReferenceExpr) left).getName();
                        if (fields.contains(name)) {
                            violation = true;
                        }
                    }
                }
            }
            if (violation) {
                addViolation(data, ifStatement);
            }
        }
        return data;
    }
}
