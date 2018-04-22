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


    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {

        String a = node.getImage(); // Get the name of the Class it's part of
        System.out.println(a);

        List<ASTMethodDeclaration> methods = node.findDescendantsOfType(ASTMethodDeclaration.class); // Find the name of methods in it

        System.out.println(methods);

        int count = 0;
        for (ASTMethodDeclaration method : methods) {

            System.out.println(method.getName());
            if (method.getName().equals("getInstance")) {
                count++;
            }

        }

        if (count > 1) {
            System.out.println("error");
            addViolation(data, node);
        }

        /*
        Can now check if each class has only one getInstance methods than it's all sorted.
        */

        return super.visit(node, data);

    }
}
