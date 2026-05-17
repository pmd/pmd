/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import static net.sourceforge.pmd.lang.java.ast.JModifier.STATIC;
import static net.sourceforge.pmd.lang.java.ast.ModifierOwner.Visibility.V_PRIVATE;
import static net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils.isMainMethod;
import static net.sourceforge.pmd.util.CollectionUtil.setOf;

import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.ast.ASTBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTInitializer;
import net.sourceforge.pmd.lang.java.ast.ASTMemberValuePair;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.ModifierOwner;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;

public class UseUtilityClassRule extends AbstractJavaRulechainRule {

    private static final String LOMBOK_UTILITY_CLASS = "lombok.experimental.UtilityClass";
    private static final String LOMBOK_NO_ARGS_CONSTRUCTOR = "lombok.NoArgsConstructor";

    public UseUtilityClassRule() {
        super(ASTClassDeclaration.class);
    }

    @Override
    public Object visit(ASTClassDeclaration klass, Object data) {
        if (klass.isInterface()
            || klass.isAbstract()
            || klass.getSuperClassTypeNode() != null
            || klass.getSuperInterfaceTypeNodes().nonEmpty()
        ) {
            return data;
        }

        if (allNonPrivateMembersAreStatic(klass) && hasWrongKindOfConstructor(klass)) {
            asCtx(data).addViolation(klass);
        }
        return null;
    }

    private boolean allNonPrivateMembersAreStatic(ASTClassDeclaration klass) {
        boolean hasNonPrivateMembers = false;

        for (ASTBodyDeclaration declaration : klass.getDeclarations()) {
            if (isMainMethod(declaration)) {
                return false;
            }

            if (declaration instanceof ASTFieldDeclaration
                    || declaration instanceof ASTMethodDeclaration
                    || declaration instanceof ASTClassDeclaration
            ) {
                ModifierOwner modifierOwner = (ModifierOwner) declaration;

                if (modifierOwner.getVisibility() != V_PRIVATE) {
                    hasNonPrivateMembers = true;
                }
                if (!modifierOwner.hasModifiers(STATIC)) {
                    return false;
                }
            } else if (declaration instanceof ASTInitializer) {
                ASTInitializer initializer = (ASTInitializer) declaration;

                if (!initializer.isStatic()) {
                    return false;
                }
            }
        }
        return hasNonPrivateMembers;
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
                && !JavaAstUtils.hasAnyAnnotation(klass, setOf(LOMBOK_UTILITY_CLASS));

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
