/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;


import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

/**
 * Returns Checks if the singleton rule is used properly.
 */
public class SingleMethodSingletonRule extends AbstractJavaRule {

    /**
     * Checks for getInstance method usage in the same class.
     * @param node of ASTCLass
     * @param data of Object
     * @return Object
     *
     */


    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {


        List<ASTMethodDeclaration> methods = node.findDescendantsOfType(ASTMethodDeclaration.class); // Find the name of methods in it

        int count = 0;
        for (ASTMethodDeclaration method : methods) {

            if (method.getName().equals("getInstance")) {
                count++;
                if (count > 1) {
                    addViolation(data, node);
                    break;
                }
            }

        }


        return super.visit(node, data);

    }
}
