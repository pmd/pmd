/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import static net.sourceforge.pmd.lang.java.ast.ModifierOwner.Visibility.V_PRIVATE;
import static net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils.hasAnyAnnotation;
import static net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil.isUtilityClass;
import static net.sourceforge.pmd.util.CollectionUtil.setOf;

import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.ast.ASTClassDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMemberValuePair;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.reporting.RuleContext;

public class InstantiableUtilityClassRule extends AbstractJavaRulechainRule {

    private static final String LOMBOK_UTILITY_CLASS = "lombok.experimental.UtilityClass";
    private static final String LOMBOK_NO_ARGS_CONSTRUCTOR = "lombok.NoArgsConstructor";

    public InstantiableUtilityClassRule() {
        super(ASTClassDeclaration.class);
    }

    @Override
    public Object visit(ASTClassDeclaration klass, Object data) {
        RuleContext ctx = (RuleContext) data;

        if (isUtilityClass(klass) && hasWrongKindOfConstructor(klass)) {
            ctx.addViolation(klass);
        }
        return null;
    }

    private boolean hasWrongKindOfConstructor(ASTClassDeclaration klass) {
        boolean hasNonPrivateCtor = false;
        boolean hasAnyCtor = false;

        for (ASTConstructorDeclaration constructorDeclaration : klass.getDeclarations(ASTConstructorDeclaration.class)) {
            hasAnyCtor = true;
            if (constructorDeclaration.getVisibility() != V_PRIVATE) {
                hasNonPrivateCtor = true;
            }
        }

        // account for default ctor
        hasNonPrivateCtor |= !hasAnyCtor
                && klass.getVisibility() != V_PRIVATE
                && !hasLombokPrivateCtor(klass)
                && !hasAnyAnnotation(klass, setOf(LOMBOK_UTILITY_CLASS));

        return hasNonPrivateCtor;
    }

    private boolean hasLombokPrivateCtor(ASTClassDeclaration parent) {
        // check if there's a lombok no arg private constructor, if so skip the rest of the rules

        return parent.getDeclaredAnnotations()
                     .filter(t -> TypeTestUtil.isA(LOMBOK_NO_ARGS_CONSTRUCTOR, t))
                     .flatMap(ASTAnnotation::getMembers)
                     // to set the access level of a constructor in lombok, you set the access property on the annotation
                     .filterMatching(ASTMemberValuePair::getName, "access")
                     // This is from the AccessLevel enum in Lombok
                     // if the constructor is found and the accesslevel is private no need to check anything else
                     .any(it -> isAccessToVarWithName(it.getValue(), "PRIVATE"));
    }

    private static boolean isAccessToVarWithName(JavaNode node, String name) {
        return node instanceof ASTNamedReferenceExpr
                && ((ASTNamedReferenceExpr) node).getName().equals(name);
    }

}
