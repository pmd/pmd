/*
 * Created on 14.07.2004
 */
package net.sourceforge.pmd.dfa.variableaccess;

import net.sourceforge.pmd.ast.ASTClassOrInterfaceBodyDeclaration;
import net.sourceforge.pmd.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.ast.ASTFormalParameter;
import net.sourceforge.pmd.ast.ASTFormalParameters;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.ASTVariableInitializer;
import net.sourceforge.pmd.ast.JavaParserVisitorAdapter;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.dfa.IDataFlowNode;
import net.sourceforge.pmd.dfa.StartOrEndDataFlowNode;
import net.sourceforge.pmd.symboltable.NameOccurrence;
import net.sourceforge.pmd.symboltable.VariableNameDeclaration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author raik, Sven Jacob
 *         <p/>
 *         Searches for special nodes and computes based on the sequence, the type of
 *         access of a variable.
 */
public class VariableAccessVisitor extends JavaParserVisitorAdapter {

    public void compute(ASTMethodDeclaration node) {
        if (node.jjtGetParent() instanceof ASTClassOrInterfaceBodyDeclaration) {
            this.computeNow(node);
        }
    }

    public void compute(ASTConstructorDeclaration node) {
        this.computeNow(node);
    }

    private void computeNow(SimpleNode node) {
        IDataFlowNode inode = node.getDataFlowNode();

        List undefinitions = markUsages(inode);

        // all variables are first in state undefinition 
        IDataFlowNode firstINode = (IDataFlowNode) inode.getFlow().get(0);
        firstINode.setVariableAccess(undefinitions);

        // all variables are getting undefined when leaving scope
        IDataFlowNode lastINode = (IDataFlowNode) inode.getFlow().get(inode.getFlow().size() - 1);
        lastINode.setVariableAccess(undefinitions);
    }

    private List markUsages(IDataFlowNode inode) {
        // undefinitions was once a field... seems like it works fine as a local
        List undefinitions = new ArrayList();
        Set variableDeclarations = collectDeclarations(inode);
        for (Iterator i = variableDeclarations.iterator(); i.hasNext();) {
            Map declarations = (Map) i.next();
            for (Iterator j = declarations.entrySet().iterator(); j.hasNext();) {
				Map.Entry entry = (Map.Entry) j.next();
                VariableNameDeclaration vnd = (VariableNameDeclaration) entry.getKey();

                if (vnd.getAccessNodeParent() instanceof ASTFormalParameter) {
                    // add definition for parameters
                    addVariableAccess(
                            (SimpleNode)vnd.getNode().getFirstParentOfType(ASTFormalParameters.class), 
                            new VariableAccess(VariableAccess.DEFINITION, vnd.getImage()), 
                            inode.getFlow());
                } else if (vnd.getAccessNodeParent().getFirstChildOfType(ASTVariableInitializer.class) != null) {
                    // add definition for initialized variables
                    addVariableAccess(
                            vnd.getNode(), 
                            new VariableAccess(VariableAccess.DEFINITION, vnd.getImage()), 
                            inode.getFlow());                    
                }
                undefinitions.add(new VariableAccess(VariableAccess.UNDEFINITION, vnd.getImage()));

                for (Iterator k = ((List) entry.getValue()).iterator(); k.hasNext();) {
                    addAccess(k, inode);
                }
            }
        }
        return undefinitions;
    }

    private Set collectDeclarations(IDataFlowNode inode) {
        Set decls = new HashSet();
        Map varDecls;
        for (int i = 0; i < inode.getFlow().size(); i++) {
            IDataFlowNode n = (IDataFlowNode) inode.getFlow().get(i);
            if (n instanceof StartOrEndDataFlowNode) {
                continue;
            }
            varDecls = n.getSimpleNode().getScope().getVariableDeclarations();
            if (!decls.contains(varDecls)) {
                decls.add(varDecls);
            }
        }
        return decls;
    }

    private void addAccess(Iterator k, IDataFlowNode inode) {
        NameOccurrence occurrence = (NameOccurrence) k.next();
        if (occurrence.isOnLeftHandSide()) {
            this.addVariableAccess(occurrence.getLocation(), new VariableAccess(VariableAccess.DEFINITION, occurrence.getImage()), inode.getFlow());
        } else if (occurrence.isOnRightHandSide() || (!occurrence.isOnLeftHandSide() && !occurrence.isOnRightHandSide())) {
            this.addVariableAccess(occurrence.getLocation(), new VariableAccess(VariableAccess.REFERENCING, occurrence.getImage()), inode.getFlow());
        }
    }

    /**
     * Adds a VariableAccess to a dataflow node.
     * @param node location of the access of a variable
     * @param va variable access to add
     * @param flow dataflownodes that can contain the node. 
     */
    private void addVariableAccess(SimpleNode node, VariableAccess va, List flow) {
        // backwards to find the right inode (not a method declaration) 
        for (int i = flow.size()-1; i > 0; i--) { 
            IDataFlowNode inode = (IDataFlowNode) flow.get(i);
            if (inode.getSimpleNode() == null) {
                continue;
            }

            List children = inode.getSimpleNode().findChildrenOfType(node.getClass());
            Iterator childrenIterator = children.iterator();
            while (childrenIterator.hasNext()) {
                if (node.equals(childrenIterator.next())) { 
                    List v = new ArrayList();
                    v.add(va);
                    inode.setVariableAccess(v);     
                    return;
                }
            }
        }
    }

}
