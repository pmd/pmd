/*
 * User: tom
 * Date: Jul 12, 2002
 * Time: 12:32:43 PM
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTCastExpression;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTPrimitiveType;
import net.sourceforge.pmd.symboltable.TypeSet;

public class UnnecessaryCastRule extends AbstractRule {

    private boolean inCastCtx;


    // TODO look for things that involve casts:
    // AssignmentExpression: int x = (int)2;
    // ArgumentList: System.out.println((int)2);

    public Object visit(ASTCastExpression node, Object data) {
        inCastCtx = true;
        super.visit(node, data);
        inCastCtx = false;
        return data;
    }

    public Object visit(ASTName node, Object data) {
        try {
            if (inCastCtx) {
                TypeSet t = new TypeSet();
                System.out.println(t.findClass(node.getImage()));
            }
        } catch (Exception e) {
        }
        return super.visit(node, data);
    }

    public Object visit(ASTPrimitiveType node, Object data) {
        try {
            if (inCastCtx) {
                TypeSet t = new TypeSet();
                System.out.println(t.findClass(node.getImage()));
            }
        } catch (Exception e) {
        }
        return super.visit(node, data);
    }
}
