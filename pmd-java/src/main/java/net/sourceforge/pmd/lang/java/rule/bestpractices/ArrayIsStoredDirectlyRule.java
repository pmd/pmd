/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTArrayAllocation;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.AccessType;
import net.sourceforge.pmd.lang.java.ast.ASTAssignmentExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTEnumBody;
import net.sourceforge.pmd.lang.java.ast.ASTEnumConstant;
import net.sourceforge.pmd.lang.java.ast.ASTExecutableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTExplicitConstructorInvocation;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTNullLiteral;
import net.sourceforge.pmd.lang.java.ast.InvocationNode;
import net.sourceforge.pmd.lang.java.ast.ModifierOwner.Visibility;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.types.OverloadSelectionResult;
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
        if (method.getBody() == null) {
            return;
        }

        // constructors of enums are implicitly private and cannot be made
        // accessible on purpose, so the allowance below would silently disable
        // the rule for all of them
        ASTEnumBody enumBody = method instanceof ASTConstructorDeclaration && method.getParent() instanceof ASTEnumBody
                               ? (ASTEnumBody) method.getParent()
                               : null;
        if (enumBody == null && getProperty(ALLOW_PRIVATE)
            && method.getVisibility() == Visibility.V_PRIVATE) {
            return;
        }

        nextFormal:
        for (ASTFormalParameter formal : method.getFormalParameters()) {
            if (formal.getTypeMirror().isArray()) {
                if (enumBody != null && !isExternalArrayPassedToEnumConstructor(enumBody, (ASTConstructorDeclaration) method, formal)) {
                    // only enum constants (or sibling constructors) in the enum body can call this
                    // constructor; if every call passes varargs elements or a freshly created array,
                    // no external reference to the stored array can exist
                    continue;
                }
                for (ASTNamedReferenceExpr usage : formal.getVarId().getLocalUsages()) {
                    // We assume usages order corresponds to control-flow order
                    // This may not hold, but it's as precise as the rule was before 7.0
                    if (usage.getAccessType() == AccessType.WRITE) {
                        continue nextFormal; // variable is overwritten
                    }

                    // the RHS of an assignment
                    if (usage.getParent() instanceof ASTAssignmentExpression && usage.getIndexInParent() == 1) {
                        ASTAssignableExpr assigned = ((ASTAssignmentExpression) usage.getParent()).getLeftOperand();
                        if (JavaAstUtils.isRefToFieldOfThisInstance(assigned)) {
                            context.addViolation(usage.getParent(), usage.getName());
                        }
                    }
                }
            }
        }
    }

    /**
     * Returns true if some call to {@code constructor} within {@code enumBody}
     * passes an existing array expression to {@code formal} rather than varargs
     * elements or a freshly allocated array (e.g. {@code new String[]{"a"}}).
     */
    private boolean isExternalArrayPassedToEnumConstructor(ASTEnumBody enumBody,
                                                           ASTConstructorDeclaration constructor,
                                                           ASTFormalParameter formal) {
        List<ASTFormalParameter> formals = constructor.getFormalParameters().toList();
        int index = formals.indexOf(formal);
        for (ASTEnumConstant constant : enumBody.children(ASTEnumConstant.class)) {
            if (passesExternalArray(constant, constructor, formals, index)) {
                return true;
            }
        }
        for (ASTConstructorDeclaration otherCtor : enumBody.children(ASTConstructorDeclaration.class)) {
            ASTExplicitConstructorInvocation explicit = otherCtor.getBody().firstChild(ASTExplicitConstructorInvocation.class);
            if (explicit != null && explicit.isThis() && passesExternalArray(explicit, constructor, formals, index)) {
                return true;
            }
        }
        return false;
    }

    private boolean passesExternalArray(InvocationNode invocation,
                                        ASTConstructorDeclaration constructor,
                                        List<ASTFormalParameter> formals,
                                        int index) {
        ASTArgumentList arguments = invocation.getArguments();
        if (arguments == null) {
            return false;
        }
        OverloadSelectionResult overload = invocation.getOverloadSelectionInfo();
        if (!overload.isFailed()) {
            if (!constructor.getSymbol().equals(overload.getMethodType().getSymbol())) {
                return false;
            }
            if (overload.isVarargsCall()) {
                return false;
            }
            if (index >= arguments.size()) {
                return false;
            }
            ASTExpression argument = arguments.get(index);
            return !(argument instanceof ASTArrayAllocation || argument instanceof ASTNullLiteral);
        }
        if (arguments.size() != formals.size()) {
            // either no arguments at all or a list of varargs elements
            return false;
        }
        ASTExpression argument = arguments.get(index);
        return argument.getTypeMirror().isArray() && !(argument instanceof ASTArrayAllocation);
    }

}
