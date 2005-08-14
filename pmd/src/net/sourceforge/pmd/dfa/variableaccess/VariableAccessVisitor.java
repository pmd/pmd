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

/**
 * @author raik
 *         <p/>
 *         Searches for special nodes and computes based on the sequence, the type of
 *         access of a variable.
 */
public class VariableAccessVisitor extends JavaParserVisitorAdapter {

    private List undefList = new Vector();

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
        IDataFlowNode firstINode = (IDataFlowNode) inode.getFlow().get(0);
        IDataFlowNode lastINode = (IDataFlowNode) inode.getFlow().get(inode.getFlow().size() - 1);

        Set variableDeclarations = new HashSet();
        /*
         * Fills the HashSet with all VariableDeclarations(Map) of all scopes
         * of this data flow (method/constructor). Adds no duplicated VariablesDeclarations
         * into the HashSet.
         * */
        for (int i = 0; i < inode.getFlow().size(); i++) {
            IDataFlowNode n = (IDataFlowNode) inode.getFlow().get(i);
            if (n instanceof StartOrEndDataFlowNode) {
                continue;
            }
            if (!variableDeclarations.contains(n.getSimpleNode().getScope().getVariableDeclarations())) {
                variableDeclarations.add(n.getSimpleNode().getScope().getVariableDeclarations());
            }
        }

        /*
         * for all VariablesDeclarations of all scopes
         * */
        for (Iterator i = variableDeclarations.iterator(); i.hasNext();) {
            Map declarations = (Map)i.next();
            for (Iterator j = declarations.keySet().iterator(); j.hasNext();) {
                VariableNameDeclaration vnd = (VariableNameDeclaration) j.next();
                this.addVariableAccess(vnd.getNode().getBeginLine(),
                        new VariableAccess(VariableAccess.DEFINITION, vnd.getImage()),
                        inode.getFlow());
                this.undefList.add(new VariableAccess(VariableAccess.UNDEFINITION,
                        vnd.getImage()));
                List values = (List) declarations.get(vnd);
                for (int g = 0; g < values.size(); g++) {
                    NameOccurrence no = (NameOccurrence) values.get(g);

                    if (no.isOnLeftHandSide()) {
                        this.addVariableAccess(no.getLocation().getBeginLine(),
                                new VariableAccess(VariableAccess.DEFINITION, no.getImage()),
                                inode.getFlow());
                    }

                    if (no.isOnRightHandSide()) {
                        this.addVariableAccess(no.getLocation().getBeginLine(),
                                new VariableAccess(VariableAccess.REFERENCING, no.getImage()),
                                inode.getFlow());
                    }

                    if (!no.isOnLeftHandSide() && !no.isOnRightHandSide()) {
                        this.addVariableAccess(no.getLocation().getBeginLine(),
                                new VariableAccess(VariableAccess.REFERENCING, no.getImage()),
                                inode.getFlow());
                    }
                }
            }
        }

        firstINode.setVariableAccess(this.undefList);
        lastINode.setVariableAccess(this.undefList);
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
