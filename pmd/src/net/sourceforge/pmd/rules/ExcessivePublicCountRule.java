/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.ast.ASTCompilationUnit;
import net.sourceforge.pmd.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.ast.AccessNode;
import net.sourceforge.pmd.rules.design.ExcessiveNodeCountRule;

/**
 * @author aglover
 *
 * Class Name: ExcessivePublicCountRule
 *
 * Rule attempts to count all public methods and public attributes defined in a class.
 *
 * If a class has a high number of public operations, it might be wise to consider whether
 * it would be appropriate to divide it into subclasses.
 *
 * A large proportion of public members and operations means the class has high potential to be
 * affected by external classes. Futhermore, increased effort will be required to
 * thoroughly test the class.
 */
public class ExcessivePublicCountRule extends ExcessiveNodeCountRule {

    public ExcessivePublicCountRule() {
        super(ASTCompilationUnit.class);
    }

    /**
     * Method counts ONLY public methods.
     */
    public Object visit(ASTMethodDeclarator node, Object data) {
        return this.getTallyOnAccessType((AccessNode) node.jjtGetParent());
    }

    /**
     * Method counts ONLY public class attributes which are not PUBLIC and
     * static- these usually represent constants....
     */
    public Object visit(ASTFieldDeclaration node, Object data) {
        if (node.isFinal() && node.isStatic()) {
            return new Integer(0);
        } else {
            return this.getTallyOnAccessType(node);
        }
    }

    /**
     * Method counts a node if it is public
     * @param AccessNode node
     * @return Integer 1 if node is public 0 otherwise
     */
    private Integer getTallyOnAccessType(AccessNode node) {
        if (node.isPublic()) {
            return new Integer(1);
        }
        return new Integer(0);
    }
}
