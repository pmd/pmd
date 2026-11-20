/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.Node;
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

                    // the RHS of an assignment, possibly through
                    // identity-preserving calls like
                    // Collections.singletonList(a).get(0)
                    ASTExpression stored = unwrapIdentityPreservingCalls(usage);
                    if (stored != null && stored.getParent() instanceof ASTAssignmentExpression
                        && stored.getIndexInParent() == 1) {
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
     * Unwraps identity-preserving calls around a parameter usage, e.g.
     * {@code Collections.singletonList(a).get(0)} or {@code List.of(a).get(0)},
     * which still denote the very same array as the parameter. Returns the
     * outermost expression that still denotes the parameter, or {@code null}
     * if the usage is wrapped in anything else.
     */
    private static @Nullable ASTExpression unwrapIdentityPreservingCalls(ASTNamedReferenceExpr usage) {
        ASTExpression current = usage;
        boolean wrappedInSingleElementList = false;
        while (true) {
            Node parent = current.getParent();
            if (parent instanceof ASTArgumentList) {
                Node call = parent.getParent();
                if (call instanceof ASTMethodCall && !wrappedInSingleElementList
                    && isSingleElementListFactory((ASTMethodCall) call, current)) {
                    current = (ASTMethodCall) call;
                    wrappedInSingleElementList = true;
                    continue;
                }
                return null;
            }
            if (parent instanceof ASTMethodCall) {
                ASTMethodCall call = (ASTMethodCall) parent;
                if (call.getQualifier() == current && wrappedInSingleElementList
                    && "get".equals(call.getMethodName())
                    && call.getArguments().size() == 1
                    && JavaAstUtils.isLiteralInt(call.getArguments().get(0), 0)) {
                    // get(0) on a single-element list returns the element itself
                    current = call;
                    wrappedInSingleElementList = false;
                    continue;
                }
                return null;
            }
            if (parent instanceof ASTCastExpression) {
                current = (ASTCastExpression) parent;
                continue;
            }
            // the outermost expression still denotes the parameter itself,
            // unless we stopped inside a single-element list, which is a
            // List, not the array (e.g. this.list = singletonList(a))
            return wrappedInSingleElementList ? null : current;
        }
    }

    /**
     * Returns whether the call is a single-element list factory wrapping the
     * given argument, i.e. {@code Collections.singletonList(a)} or
     * {@code List.of(a)}. Only these are handled: other wrappers such as
     * {@code Arrays.asList(a)} are not treated as identity-preserving here.
     */
    private static boolean isSingleElementListFactory(ASTMethodCall call, ASTExpression arg) {
        if (call.getArguments().size() != 1 || call.getArguments().get(0) != arg) {
            return false;
        }
        if ("singletonList".equals(call.getMethodName())) {
            return TypeTestUtil.isDeclaredInClass(java.util.Collections.class, call.getMethodType());
        }
        if ("of".equals(call.getMethodName())) {
            return TypeTestUtil.isDeclaredInClass(java.util.List.class, call.getMethodType());
        }
        return false;
    }

}
