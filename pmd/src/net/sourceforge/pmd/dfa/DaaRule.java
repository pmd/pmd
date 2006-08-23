/*
 * Created on 20.07.2004
 */
package net.sourceforge.pmd.dfa;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.dfa.pathfinder.CurrentPath;
import net.sourceforge.pmd.dfa.pathfinder.DAAPathFinder;
import net.sourceforge.pmd.dfa.pathfinder.Executable;
import net.sourceforge.pmd.dfa.variableaccess.VariableAccess;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;

/**
 * @author raik
 *         <p/>
 *         Starts path search for each method and runs code if found.
 */
public class DaaRule extends AbstractRule implements Executable {

    private RuleContext rc;
    private int counter;
    private static final int MAX_PATHS = 5000;

    public Object visit(ASTMethodDeclaration node, Object data) {
        this.rc = (RuleContext) data;
        counter = 0;

        IDataFlowNode n = (IDataFlowNode) node.getDataFlowNode().getFlow().get(0);
        System.out.println("In DaaRule, IDataFlowNode n = " + n);

        DAAPathFinder a = new DAAPathFinder(n, this);
        a.run();

        super.visit(node, data);
        return data;
    }

    public void execute(CurrentPath path) {
        Hashtable hash = new Hashtable();
        counter++;
        if (counter == 5000) {
            System.out.print("|");
            counter = 0;
        }
        for (Iterator d = path.iterator(); d.hasNext();) {
            IDataFlowNode inode = (IDataFlowNode) d.next();
            if (inode.getVariableAccess() != null) {
                for (int g = 0; g < inode.getVariableAccess().size(); g++) {
                    VariableAccess va = (VariableAccess) inode.getVariableAccess().get(g);

                    Object o = hash.get(va.getVariableName());
                    if (o != null) {
                        List array = (List) o;
                        // get the last access type
                        int last = ((Integer) array.get(0)).intValue();

                        if (va.accessTypeMatches(last) && va.isDefinition()) { // DD
                        	if (inode.getSimpleNode() != null) {
                        		// preventing NullpointerException
                        		addDaaViolation(rc, inode.getSimpleNode(), "DD", va.getVariableName());
                        	}
                        } else if (last == VariableAccess.UNDEFINITION && va.isReference()) { // UR
                        	if (inode.getSimpleNode() != null) {
                        		// preventing NullpointerException                        		
                        		addDaaViolation(rc, inode.getSimpleNode(), "UR", va.getVariableName());
                        	}
                        } else if (last == VariableAccess.DEFINITION && va.isUndefinition()) { // DU
                        	if (inode.getSimpleNode() != null) {
                        		addDaaViolation(rc, inode.getSimpleNode(), "DU", va.getVariableName());
                        	} else {
                        		// undefinition outside, get the node of the definition
                        		SimpleNode lastSimpleNode = (SimpleNode)array.get(1);
                        		if (lastSimpleNode != null) {
                            		addDaaViolation(rc, lastSimpleNode, "DU", va.getVariableName());                           		
                            	}
                        	}
                        }
                    }
                    List array = new ArrayList();
                    array.add(new Integer(va.getAccessType()));
                    array.add(inode.getSimpleNode());
                    hash.put(va.getVariableName(), array);
                }
            }
        }
    }
    
    /**
     * Adds a daa violation to the report.
     *
     * @param ctx  the RuleContext
     * @param node the node that produces the violation
     * @param msg  specific message to put in the report
     */
    private final void addDaaViolation(Object data, SimpleNode node, String msg, String var) {
        RuleContext ctx = (RuleContext) data;
        ctx.getReport().addRuleViolation(new DaaRuleViolation(this, ctx, node, msg, var));
    }
}
