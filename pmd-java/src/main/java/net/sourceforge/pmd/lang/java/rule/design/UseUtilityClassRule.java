/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import static net.sourceforge.pmd.util.CollectionUtil.setOf;

import java.util.Set;

import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.ast.ASTBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMemberValuePair;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.AccessNode.Visibility;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;

public class UseUtilityClassRule extends AbstractJavaRulechainRule {

    private static final Set<String> IGNORED_CLASS_ANNOT = setOf(
        "lombok.experimental.UtilityClass",
        "org.junit.runner.RunWith" // for suites and such
    );

    public UseUtilityClassRule() {
        super(ASTClassOrInterfaceDeclaration.class);
    }

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration klass, Object data) {
        if (JavaRuleUtil.hasAnyAnnotation(klass, IGNORED_CLASS_ANNOT)
            || TypeTestUtil.isA("junit.framework.TestSuite", klass) // suite method is ok
            || klass.isInterface()
            || klass.isAbstract()
            || klass.getSuperClassTypeNode() != null
            || klass.getSuperInterfaceTypeNodes().nonEmpty()
            //    || JavaRuleUtil.isUtilityClass(node)
        ) {
            return data;
        }

        boolean hasAnyMethods = false;
        boolean hasNonPrivateCtor = false;
        boolean hasAnyCtor = false;
        for (ASTBodyDeclaration declaration : klass.getDeclarations()) {
            if (declaration instanceof ASTFieldDeclaration
                && !((ASTFieldDeclaration) declaration).isStatic()) {
                return null;
            }
            if (declaration instanceof ASTConstructorDeclaration) {
                hasAnyCtor = true;
                if (((ASTConstructorDeclaration) declaration).getVisibility() != Visibility.V_PRIVATE) {
                    hasNonPrivateCtor = true;
                }
            }

            if (declaration instanceof ASTMethodDeclaration) {
                if (((ASTMethodDeclaration) declaration).getVisibility() != Visibility.V_PRIVATE) {
                    hasAnyMethods = true;
                }
                if (!((ASTMethodDeclaration) declaration).isStatic()) {
                    return null;
                }
            }
        }

        // account for default ctor
        hasNonPrivateCtor |= !hasAnyCtor
            && klass.getVisibility() != Visibility.V_PRIVATE
            && !hasLombokPrivateCtor(klass);


        String message;
        if (hasAnyMethods && hasNonPrivateCtor) {
            message = "This utility class has a non-private constructor";
            addViolationWithMessage(data, klass, message);
        }
        return null;
    }

    private boolean hasLombokPrivateCtor(ASTClassOrInterfaceDeclaration parent) {
        // check if there's a lombok no arg private constructor, if so skip the rest of the rules

        return parent.getDeclaredAnnotations()
                     .filter(t -> TypeTestUtil.isA("lombok.NoArgsConstructor", t))
                     .flatMap(ASTAnnotation::getMembers)
                     // to set the access level of a constructor in lombok, you set the access property on the annotation
                     .filterMatching(ASTMemberValuePair::getName, "access")
                     // This is from the AccessLevel enum in Lombok
                     // if the constructor is found and the accesslevel is private no need to check anything else
                     .any(it -> isAccessToVarWithName(it.getValue(), "PRIVATE"));
    }

    private static boolean isAccessToVarWithName(JavaNode node, String name) {
        if (node instanceof ASTNamedReferenceExpr) {
            return ((ASTNamedReferenceExpr) node).getName().equals(name);
        }
        return false;
    }

}
