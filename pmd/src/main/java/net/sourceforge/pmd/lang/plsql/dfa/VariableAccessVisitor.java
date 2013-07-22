/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.plsql.dfa;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.dfa.DataFlowNode;
import net.sourceforge.pmd.lang.dfa.StartOrEndDataFlowNode;
import net.sourceforge.pmd.lang.dfa.VariableAccess;
import net.sourceforge.pmd.lang.plsql.ast.ASTCompoundTriggerBlock;
import net.sourceforge.pmd.lang.plsql.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.plsql.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.plsql.ast.ASTPackageBody;
import net.sourceforge.pmd.lang.plsql.ast.ASTProgramUnit;
import net.sourceforge.pmd.lang.plsql.ast.ASTTriggerTimingPointSection;
import net.sourceforge.pmd.lang.plsql.ast.ASTTriggerUnit;
import net.sourceforge.pmd.lang.plsql.ast.ASTTypeMethod;
import net.sourceforge.pmd.lang.plsql.ast.ASTVariableOrConstantInitializer;
import net.sourceforge.pmd.lang.plsql.ast.PLSQLNode;
import net.sourceforge.pmd.lang.plsql.ast.PLSQLParserVisitorAdapter;
import net.sourceforge.pmd.lang.plsql.symboltable.NameOccurrence;
import net.sourceforge.pmd.lang.plsql.symboltable.VariableNameDeclaration;
//import net.sourceforge.pmd.lang.plsql.ast.ASTConstructorDeclaration;

/**
 * @author raik, Sven Jacob
 *         <p/>
 *         Searches for special nodes and computes based on the sequence, the type of
 *         access of a variable.
 */
public class VariableAccessVisitor extends PLSQLParserVisitorAdapter {

    public void compute(ASTMethodDeclaration node) {
	//This includes Package Bodies and Object Type Bodies
	if (node.jjtGetParent() instanceof ASTPackageBody) {
	    this.computeNow(node);
	}
    }

    public void compute(ASTProgramUnit node) {
	if (node.jjtGetParent() instanceof ASTPackageBody 
            || node.jjtGetParent() instanceof ASTCompoundTriggerBlock // Treat Compound Trigger as a Package Body 
           ) {
	    this.computeNow(node);
	}
    }

    public void compute(ASTTypeMethod node) {
	if (node.jjtGetParent() instanceof ASTPackageBody) {
	    this.computeNow(node);
	}
    }

    public void compute(ASTTriggerUnit node) {
	    this.computeNow(node);
    }

    public void compute(ASTTriggerTimingPointSection node) {
	    this.computeNow(node);
    }

    /* SRT public void compute(ASTConstructorDeclaration node) {
	this.computeNow(node);
    }*/

    private void computeNow(Node node) {
	DataFlowNode inode = node.getDataFlowNode();

	List<VariableAccess> undefinitions = markUsages(inode);

	// all variables are first in state undefinition
	DataFlowNode firstINode = inode.getFlow().get(0);
	firstINode.setVariableAccess(undefinitions);

	// all variables are getting undefined when leaving scope
	DataFlowNode lastINode = inode.getFlow().get(inode.getFlow().size() - 1);
	lastINode.setVariableAccess(undefinitions);
    }

    private List<VariableAccess> markUsages(DataFlowNode inode) {
	// undefinitions was once a field... seems like it works fine as a local
	List<VariableAccess> undefinitions = new ArrayList<VariableAccess>();
	Set<Map<VariableNameDeclaration, List<NameOccurrence>>> variableDeclarations = collectDeclarations(inode);
	for (Map<VariableNameDeclaration, List<NameOccurrence>> declarations : variableDeclarations) {
	    for (Map.Entry<VariableNameDeclaration, List<NameOccurrence>> entry : declarations.entrySet()) {
		VariableNameDeclaration vnd = entry.getKey();

		if (vnd.getNode().jjtGetParent() instanceof ASTFormalParameter) {
		    // no definition/undefinition/references for parameters
		    continue;
		} else if (  vnd.getNode().jjtGetParent().getFirstDescendantOfType(ASTVariableOrConstantInitializer.class) != null) {
		    // add definition for initialized variables
		    addVariableAccess(vnd.getNode(), new VariableAccess(VariableAccess.DEFINITION, vnd.getImage()),
			    inode.getFlow());
		}
		undefinitions.add(new VariableAccess(VariableAccess.UNDEFINITION, vnd.getImage()));

		for (NameOccurrence occurrence : entry.getValue()) {
		    addAccess(occurrence, inode);
		}
	    }
	}
	return undefinitions;
    }

    private Set<Map<VariableNameDeclaration, List<NameOccurrence>>> collectDeclarations(DataFlowNode inode) {
	Set<Map<VariableNameDeclaration, List<NameOccurrence>>> decls = new HashSet<Map<VariableNameDeclaration, List<NameOccurrence>>>();
	Map<VariableNameDeclaration, List<NameOccurrence>> varDecls;
	for (int i = 0; i < inode.getFlow().size(); i++) {
	    DataFlowNode n = inode.getFlow().get(i);
	    if (n instanceof StartOrEndDataFlowNode) {
		continue;
	    }
	    varDecls = ((PLSQLNode)n.getNode()).getScope().getVariableDeclarations();
	    if (!decls.contains(varDecls)) {
		decls.add(varDecls);
	    }
	}
	return decls;
    }

    private void addAccess(NameOccurrence occurrence, DataFlowNode inode) {
	if (occurrence.isOnLeftHandSide()) {
	    this.addVariableAccess(occurrence.getLocation(), new VariableAccess(VariableAccess.DEFINITION, occurrence
		    .getImage()), inode.getFlow());
	} else if (occurrence.isOnRightHandSide() || !occurrence.isOnLeftHandSide() && !occurrence.isOnRightHandSide()) {
	    this.addVariableAccess(occurrence.getLocation(), new VariableAccess(VariableAccess.REFERENCING, occurrence
		    .getImage()), inode.getFlow());
	}
    }

    /**
     * Adds a VariableAccess to a dataflow node.
     * @param node location of the access of a variable
     * @param va variable access to add
     * @param flow dataflownodes that can contain the node.
     */
    private void addVariableAccess(Node node, VariableAccess va, List<DataFlowNode> flow) {
	// backwards to find the right inode (not a method declaration)
	for (int i = flow.size() - 1; i > 0; i--) {
	    DataFlowNode inode = flow.get(i);
	    if (inode.getNode() == null) {
		continue;
	    }

	    List<? extends Node> children = inode.getNode().findDescendantsOfType(node.getClass());
	    for (Node n : children) {
		if (node.equals(n)) {
		    List<VariableAccess> v = new ArrayList<VariableAccess>();
		    v.add(va);
		    inode.setVariableAccess(v);
		    return;
		}
	    }
	}
    }

}
