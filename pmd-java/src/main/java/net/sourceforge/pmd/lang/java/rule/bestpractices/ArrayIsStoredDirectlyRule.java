/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.AccessType;
import net.sourceforge.pmd.lang.java.ast.ASTAssignmentExpression;
import net.sourceforge.pmd.lang.java.ast.ASTCastExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTExecutableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.ModifierOwner.Visibility;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;
import net.sourceforge.pmd.reporting.RuleContext;

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
        RuleContext ctx = (RuleContext) data;

        checkAssignments(ctx, node);
        return null;
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        RuleContext ctx = (RuleContext) data;

        checkAssignments(ctx, node);
        return null;
    }

    private void checkAssignments(RuleContext context, ASTExecutableDeclaration method) {
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

                    // the RHS of an assignment, possibly wrapped in calls
                    // that return the same value, eg
                    // Collections.singletonList(a).get(0)
                    ASTExpression stored = unwrapValuePreservingWrappers(usage);
                    if (stored.getParent() instanceof ASTAssignmentExpression && stored.getIndexInParent() == 1) {
                        ASTAssignableExpr assigned = ((ASTAssignmentExpression) stored.getParent()).getLeftOperand();
                        if (JavaAstUtils.isRefToFieldOfThisInstance(assigned)) {
                            context.addViolation(stored.getParent(), usage.getName());
                        }
                    }
                }
            }
        }
    }

    /**
     * Unwraps expressions that provably evaluate to the same value as the
     * given expression: casts, single-element container creations
     * ({@code Collections.singletonList(x)}, {@code List.of(x)}), and element
     * retrievals from such containers ({@code get(0)}, {@code getFirst()}).
     */
    private static ASTExpression unwrapValuePreservingWrappers(ASTExpression expr) {
        ASTExpression current = expr;
        boolean inSingleElementContainer = false;
        while (true) {
            JavaNode parent = current.getParent();
            if (parent instanceof ASTCastExpression && ((ASTCastExpression) parent).getOperand() == current) {
                current = (ASTExpression) parent;
            } else if (parent instanceof ASTArgumentList && parent.getParent() instanceof ASTMethodCall
                    && isSingleElementContainerCreation((ASTMethodCall) parent.getParent(), current)) {
                inSingleElementContainer = true;
                current = (ASTExpression) parent.getParent();
            } else if (parent instanceof ASTMethodCall && inSingleElementContainer
                    && isElementRetrieval((ASTMethodCall) parent, current)) {
                inSingleElementContainer = false;
                current = (ASTExpression) parent;
            } else {
                break;
            }
        }
        return current;
    }

    private static boolean isSingleElementContainerCreation(ASTMethodCall call, ASTExpression arg) {
        if (call.getArguments().size() != 1 || call.getArguments().get(0) != arg) {
            return false;
        }
        if ("singletonList".equals(call.getMethodName())
                && TypeTestUtil.isA("java.util.Collections", call.getQualifier())) {
            return true;
        }
        return "of".equals(call.getMethodName())
                && TypeTestUtil.isA("java.util.List", call.getQualifier());
    }

    private static boolean isElementRetrieval(ASTMethodCall call, ASTExpression receiver) {
        if (call.getQualifier() != receiver) {
            return false;
        }
        if ("getFirst".equals(call.getMethodName()) && call.getArguments().size() == 0) {
            return true;
        }
        return "get".equals(call.getMethodName()) && call.getArguments().size() == 1;
    }

}
