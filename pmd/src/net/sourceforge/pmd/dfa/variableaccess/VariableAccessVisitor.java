/*
 * Created on 14.07.2004
 */
package net.sourceforge.pmd.dfa.variableaccess;

import net.sourceforge.pmd.ast.ASTClassBodyDeclaration;
import net.sourceforge.pmd.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.JavaParserVisitorAdapter;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.dfa.IDataFlowNode;
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
        if (node.jjtGetParent() instanceof ASTClassBodyDeclaration) {
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


        Set scopeSet = new HashSet();
        /*
         * Fills the HashSet with all VariableDeclarations(Map) of all scopes
         * of this data flow(method). Adds no dublicated VariablesDeclarations
         * into the HashSet.
         * */
        for (int i = 0; i < inode.getFlow().size(); i++) {
            IDataFlowNode n = (IDataFlowNode) inode.getFlow().get(i);

            SimpleNode snode = n.getSimpleNode();
            if (snode == null) continue;

            if (!scopeSet.contains(snode.getScope().getVariableDeclarations())) {
                scopeSet.add(snode.getScope().getVariableDeclarations());
            }
        }

        /*
         * for all founded VariablesDeclarations of all scopes
         * */
        Iterator scopeIter = scopeSet.iterator();
        while (scopeIter.hasNext()) {
            Map map = (Map) scopeIter.next();

            // for each set of VariableDeclaration
            Iterator iter = map.keySet().iterator();
            while (iter.hasNext()) {
                VariableNameDeclaration vnd = (VariableNameDeclaration) iter.next();
                this.addVariableAccess(vnd.getLine(),
                        new VariableAccess(VariableAccess.DEFINITION, vnd.getImage()),
                        inode.getFlow());
                this.undefList.add(new VariableAccess(VariableAccess.UNDEFINITION,
                        vnd.getImage()));
                List values = (List) map.get(vnd);
                for (int g = 0; g < values.size(); g++) {
                    NameOccurrence no = (NameOccurrence) values.get(g);

                    if (no.isOnLeftHandSide()) {
                        this.addVariableAccess(no.getBeginLine(),
                                new VariableAccess(VariableAccess.DEFINITION, no.getImage()),
                                inode.getFlow());
                    }

                    if (no.isOnRightHandSide()) {
                        this.addVariableAccess(no.getBeginLine(),
                                new VariableAccess(VariableAccess.REFERENCING, no.getImage()),
                                inode.getFlow());
                    }

                    if (!no.isOnLeftHandSide() && !no.isOnRightHandSide()) {
                        this.addVariableAccess(no.getBeginLine(),
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
