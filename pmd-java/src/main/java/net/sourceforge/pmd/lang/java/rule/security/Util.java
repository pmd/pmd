/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.security;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sourceforge.pmd.lang.java.ast.ASTAllocationExpression;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;

/**
 * @author sergeygorbaty
 *
 */
public class Util {

    private Util() {
        // intentionally
    }

    public static Set<String> findVariablesPassedToAnyParam(ASTClassOrInterfaceBodyDeclaration node,
            Class<?> classToFind) {
        Set<String> passedInIvVarNames = new HashSet<>();

        // find new javax.crypto.spec.SecretKeySpec(...)
        List<ASTAllocationExpression> allocations = node.findDescendantsOfType(ASTAllocationExpression.class);
        for (ASTAllocationExpression allocation : allocations) {

            ASTClassOrInterfaceType declClassName = allocation.getFirstDescendantOfType(ASTClassOrInterfaceType.class);
            if (declClassName != null) {
                Class<?> foundClass = declClassName.getType();
                if (foundClass != null && classToFind.isAssignableFrom(foundClass)) {
                    ASTPrimaryExpression init = allocation.getFirstDescendantOfType(ASTPrimaryExpression.class);
                    if (init != null) {
                        ASTName name = init.getFirstDescendantOfType(ASTName.class);
                        if (name != null) {
                            if (name.getNameDeclaration() != null) {
                                passedInIvVarNames.add(name.getNameDeclaration().getName());
                            }
                        }
                    }
                }
            }
        }

        return passedInIvVarNames;
    }

}
