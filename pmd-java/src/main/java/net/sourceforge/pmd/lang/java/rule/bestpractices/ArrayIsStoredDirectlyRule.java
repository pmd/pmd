/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.AccessType;
import net.sourceforge.pmd.lang.java.ast.ASTAssignmentExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.AccessNode.Visibility;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;

/**
 * If a method or constructor receives an array as an argument, the array should
 * be cloned instead of directly stored. This prevents future changes from the
 * user from affecting the original array.
 */
public class ArrayIsStoredDirectlyRule extends AbstractJavaRulechainRule {

    private static final PropertyDescriptor<Boolean> ALLOW_PRIVATE =
        PropertyFactory.booleanProperty("allowPrivate")
                       .defaultValue(true)
                       .desc("If true, allow private methods/constructors to store arrays directly")
                       .build();

    public ArrayIsStoredDirectlyRule() {
        super(ASTMethodDeclaration.class, ASTConstructorDeclaration.class);
        definePropertyDescriptor(ALLOW_PRIVATE);
    }

    @Override
    public Object visit(ASTConstructorDeclaration node, Object data) {
        checkAssignments((RuleContext) data, node);
        return data;
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        checkAssignments((RuleContext) data, node);
        return data;
    }

    private void checkAssignments(RuleContext context, ASTMethodOrConstructorDeclaration method) {
        if (method.getVisibility() == Visibility.V_PRIVATE && getProperty(ALLOW_PRIVATE)
            || method.getBody() == null) {
            return;
        }

        nextFormal:
        for (ASTFormalParameter formal : method.getFormalParameters()) {
            if (formal.getTypeMirror().isArray()) {
                for (ASTNamedReferenceExpr usage : formal.getVarId().getLocalUsages()) {
                    // We assume usages order corresponds to control-flow order
                    // This may not hold, but it's as precise as the rule was before 7.0
                    if (usage.getAccessType() == AccessType.WRITE) {
                        continue nextFormal; // variable is overwritten
                    }

                    // the RHS of an assignment
                    if (usage.getParent() instanceof ASTAssignmentExpression && usage.getIndexInParent() == 1) {
                        ASTAssignableExpr assigned = ((ASTAssignmentExpression) usage.getParent()).getLeftOperand();
                        if (JavaRuleUtil.isRefToFieldOfThisInstance(assigned)) {
                            addViolation(context, usage.getParent(), usage.getName());
                        }
                    }
                }
            }
        }
    }

}
