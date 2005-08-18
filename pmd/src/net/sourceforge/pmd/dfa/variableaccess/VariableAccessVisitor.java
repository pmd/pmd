/*
 * Created on 14.07.2004
 */
package net.sourceforge.pmd.dfa.variableaccess;

import net.sourceforge.pmd.ast.ASTClassOrInterfaceBodyDeclaration;
import net.sourceforge.pmd.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.JavaParserVisitorAdapter;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.dfa.IDataFlowNode;
import net.sourceforge.pmd.dfa.StartOrEndDataFlowNode;
import net.sourceforge.pmd.symboltable.NameOccurrence;
import net.sourceforge.pmd.symboltable.VariableNameDeclaration;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.ArrayList;

/**
 * @author raik
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

        // is this necessary?  Why does the first node need undefs?
        IDataFlowNode firstINode = (IDataFlowNode) inode.getFlow().get(0);
        firstINode.setVariableAccess(undefinitions);

        IDataFlowNode lastINode = (IDataFlowNode) inode.getFlow().get(inode.getFlow().size() - 1);
        lastINode.setVariableAccess(undefinitions);
    }

    private List markUsages(IDataFlowNode inode) {
        // undefinitions was once a field... seems like it works fine as a local
        List undefinitions = new ArrayList();
        Set variableDeclarations = collectDeclarations(inode);
        for (Iterator i = variableDeclarations.iterator(); i.hasNext();) {
            Map declarations = (Map)i.next();
            for (Iterator j = declarations.keySet().iterator(); j.hasNext();) {
                VariableNameDeclaration vnd = (VariableNameDeclaration) j.next();
                addVariableAccess(vnd.getNode().getBeginLine(), new VariableAccess(VariableAccess.DEFINITION, vnd.getImage()), inode.getFlow());
                undefinitions.add(new VariableAccess(VariableAccess.UNDEFINITION, vnd.getImage()));
                for (Iterator k = ((List) declarations.get(vnd)).iterator(); k.hasNext();) {
                    addAccess(k, inode);
                }
            }
        }
        return undefinitions;
    }

    private Set collectDeclarations(IDataFlowNode inode) {
        Set decls = new HashSet();
        for (int i = 0; i < inode.getFlow().size(); i++) {
            IDataFlowNode n = (IDataFlowNode) inode.getFlow().get(i);
            if (n instanceof StartOrEndDataFlowNode) {
                continue;
            }
            if (!decls.contains(n.getSimpleNode().getScope().getVariableDeclarations())) {
                decls.add(n.getSimpleNode().getScope().getVariableDeclarations());
            }
        }
        return decls;
    }

    private void addAccess(Iterator k, IDataFlowNode inode) {
        NameOccurrence occurrence = (NameOccurrence) k.next();
        if (occurrence.isOnLeftHandSide()) {
            this.addVariableAccess(occurrence.getLocation().getBeginLine(), new VariableAccess(VariableAccess.DEFINITION, occurrence.getImage()), inode.getFlow());
        } else if (occurrence.isOnRightHandSide() || (!occurrence.isOnLeftHandSide() && !occurrence.isOnRightHandSide())) {
            this.addVariableAccess(occurrence.getLocation().getBeginLine(), new VariableAccess(VariableAccess.REFERENCING, occurrence.getImage()), inode.getFlow());
        }
    }

    private void addVariableAccess(int line, VariableAccess va, List flow) {
        for (int i = 1; i < flow.size(); i++) {
            IDataFlowNode inode = (IDataFlowNode) flow.get(i);
            if (line == inode.getLine()) {
                Vector v = new Vector();
                v.add(va);
                inode.setVariableAccess(v);
            }
        }
    }

}
