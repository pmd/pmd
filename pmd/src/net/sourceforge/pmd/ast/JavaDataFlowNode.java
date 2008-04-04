/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.ast;

import java.util.LinkedList;

import net.sourceforge.pmd.dfa.AbstractDataFlowNode;
import net.sourceforge.pmd.dfa.DataFlowNode;
import net.sourceforge.pmd.lang.ast.Node;

public class JavaDataFlowNode extends AbstractDataFlowNode {

    public JavaDataFlowNode(LinkedList<DataFlowNode> dataFlow, Node node) {
	super(dataFlow, node);
    }

    public String toString() {
	String res = "DataFlowNode: line " + this.getLine() + ", ";
	if (node instanceof ASTMethodDeclaration || node instanceof ASTConstructorDeclaration) {
	    res += (node instanceof ASTMethodDeclaration) ? "(method)" : "(constructor)";
	} else {
	    res = super.toString();
	}
	return res;
    }
}
