/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.ast.SimpleNode;

public class SymbolTableTestRule extends AbstractRule implements Rule {

    public Object visit(ASTVariableDeclaratorId node, Object data) {
        SimpleNode n = node.getTypeNameNode();
        System.out.println("n = " + n.getImage());
        return super.visit(node, data);
    }

}
