/*
 * User: tom
 * Date: Jul 12, 2002
 * Time: 12:32:43 PM
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTCastExpression;

public class UnnecessaryCastRule extends AbstractRule {

/*
    public Object visit(ASTCastExpression node, Object data) {
        System.out.println("CAST");
        return super.visit(node, data);
    }
    public Object visit(ASTName node, Object data) {

        try {
        System.out.println("name = " + node.getImage());
            //Class.forName()
        System.out.println(System.getProperty("java.class.path"));
        } catch (Exception e) {}
        return super.visit(node, data);
    }
*/

}
