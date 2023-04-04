/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import static net.sourceforge.pmd.lang.java.ast.JModifier.FINAL;
import static net.sourceforge.pmd.lang.java.ast.JModifier.NATIVE;
import static net.sourceforge.pmd.lang.java.ast.JModifier.SYNCHRONIZED;
import static net.sourceforge.pmd.properties.PropertyFactory.booleanProperty;

import java.lang.reflect.Modifier;

import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTExpressionStatement;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTList;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTStatement;
import net.sourceforge.pmd.lang.java.ast.ASTSuperExpression;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.symbols.JExecutableSymbol;
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.types.OverloadSelectionResult;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.properties.PropertyDescriptor;

public class UselessOverridingMethodRule extends AbstractJavaRulechainRule {

    // TODO extend AbstractIgnoredAnnotationRule node
    // TODO ignore if there is javadoc
    private static final PropertyDescriptor<Boolean> IGNORE_ANNOTATIONS_DESCRIPTOR =
        booleanProperty("ignoreAnnotations")
            .defaultValue(false)
            .desc("Ignore methods that have annotations (except @Override)")
            .build();


    public UselessOverridingMethodRule() {
        super(ASTMethodDeclaration.class);
        definePropertyDescriptor(IGNORE_ANNOTATIONS_DESCRIPTOR);
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        if (!node.isOverridden()
            || node.getBody() == null
            // Can skip methods which are final or have new behavior (synchronized, native)
            || node.getModifiers().hasAny(FINAL, NATIVE, SYNCHRONIZED)
            // We can also skip the 'clone' method as they are generally
            // 'useless' but as it is considered a 'good practice' to
            // implement them anyway ( see bug 1522517)
            || JavaAstUtils.isCloneMethod(node)) {
            return null;
        }

        // skip annotated methods
        if (!getProperty(IGNORE_ANNOTATIONS_DESCRIPTOR)
            && node.getDeclaredAnnotations().any(it -> !TypeTestUtil.isA(Override.class, it))) {
            return null;
        }

        ASTStatement statement = ASTList.singleOrNull(node.getBody());
        // Only process functions with one statement
        if (statement == null) {
            return null;
        }

        if ((statement instanceof ASTExpressionStatement || statement instanceof ASTReturnStatement)
            && statement.getNumChildren() == 1
            && statement.getChild(0) instanceof ASTMethodCall) {

            // merely calling super.foo() or returning super.foo()
            ASTMethodCall methodCall = (ASTMethodCall) statement.getChild(0);
            if (methodCall.getQualifier() instanceof ASTSuperExpression
                && methodCall.getArguments().size() == node.getArity()
                // might be disambiguating: Interface.super.foo()
                && JavaAstUtils.isUnqualifiedSuper(methodCall.getQualifier())) {

                OverloadSelectionResult overload = methodCall.getOverloadSelectionInfo();
                if (!overload.isFailed()
                    // note: don't compare symbols, as the equals method for method symbols is broken for now
                    && overload.getMethodType().equals(node.getOverriddenMethod())
                    && sameModifiers(node.getOverriddenMethod().getSymbol(), node.getSymbol())
                    && argumentsAreUnchanged(node, methodCall)) {
                    addViolation(data, node);
                }
            }
        }
        return null;
    }

    private boolean argumentsAreUnchanged(ASTMethodDeclaration node, ASTMethodCall methodCall) {
        ASTArgumentList arg = methodCall.getArguments();
        int i = 0;
        for (ASTFormalParameter formal : node.getFormalParameters()) {
            if (!JavaAstUtils.isReferenceToVar((ASTExpression) arg.getChild(i), formal.getVarId().getSymbol())) {
                return false;
            }
            i++;
        }
        return true;
    }

    private boolean sameModifiers(JExecutableSymbol superMethod, JMethodSymbol subMethod) {
        int visibilityMask = Modifier.PUBLIC | Modifier.PRIVATE | Modifier.PROTECTED;
        return (visibilityMask & subMethod.getModifiers()) == (visibilityMask & superMethod.getModifiers())
            // making visible in another package
            && !isProtectedElevatingVisibility(superMethod, subMethod);
    }

    private boolean isProtectedElevatingVisibility(JExecutableSymbol superMethod, JMethodSymbol subMethod) {
        return Modifier.isProtected(subMethod.getModifiers())
            && !subMethod.getPackageName().equals(superMethod.getPackageName());
    }

}
