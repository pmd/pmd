/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.AccessType;
import net.sourceforge.pmd.lang.java.ast.ASTAssignmentExpression;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTEnumBody;
import net.sourceforge.pmd.lang.java.ast.ASTEnumConstant;
import net.sourceforge.pmd.lang.java.ast.ASTExecutableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ModifierOwner.Visibility;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
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
        ASTEnumBody enumBody = method instanceof ASTConstructorDeclaration
                               ? method.ancestors(ASTEnumBody.class).first()
                               : null;
        if (enumBody == null && getProperty(ALLOW_PRIVATE)
            && method.getVisibility() == Visibility.V_PRIVATE) {
            return;
        }

        nextFormal:
        for (ASTFormalParameter formal : method.getFormalParameters()) {
            if (formal.getTypeMirror().isArray()) {
                if (enumBody != null && formal.isVarargs() && !isArrayPassedToVarargs(enumBody, method, formal)) {
                    // the compiler creates a new array for the varargs parameter,
                    // so the stored array cannot be reached by the caller (only the
                    // enum constants call this constructor)
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
     * Returns true if some enum constant passes an array to the varargs parameter,
     * e.g. {@code CONST(someArray)} instead of {@code CONST(element1, element2)}.
     * In the latter case the compiler creates the array for the constructor.
     */
    private boolean isArrayPassedToVarargs(ASTEnumBody enumBody, ASTExecutableDeclaration constructor,
                                           ASTFormalParameter varargs) {
        List<ASTFormalParameter> formals = constructor.getFormalParameters().toList();
        int index = formals.indexOf(varargs);
        for (ASTEnumConstant constant : enumBody.children(ASTEnumConstant.class)) {
            ASTArgumentList arguments = constant.getArguments();
            if (arguments == null || arguments.size() != formals.size()) {
                // either no arguments at all or a list of varargs elements
                continue;
            }
            ASTExpression argument = arguments.toList().get(index);
            if (argument.getTypeMirror().isArray()) {
                return true;
            }
        }
        return false;
    }

}
