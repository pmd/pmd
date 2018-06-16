/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.dfa;

import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.dfa.AbstractDataFlowNode;
import net.sourceforge.pmd.lang.dfa.DataFlowNode;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;

public class JavaDataFlowNode extends AbstractDataFlowNode {

    public JavaDataFlowNode(List<DataFlowNode> dataFlow, Node node) {
        super(dataFlow, node);
    }

    @Override
    public String toString() {
        String res = "DataFlowNode: line " + this.getLine() + ", ";
        if (node instanceof ASTMethodDeclaration || node instanceof ASTConstructorDeclaration) {
            res += node instanceof ASTMethodDeclaration ? "(method)" : "(constructor)";
        } else {
            res = super.toString();
        }
        return res;
    }
}
