package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.dfa.IDataFlowNode;
import net.sourceforge.pmd.dfa.pathfinder.CurrentPath;
import net.sourceforge.pmd.dfa.pathfinder.DAAPathFinder;
import net.sourceforge.pmd.dfa.pathfinder.Executable;
import net.sourceforge.pmd.dfa.variableaccess.VariableAccess;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class UselessAssignment extends AbstractRule implements Executable {

    private RuleContext rc;

    public Object visit(ASTMethodDeclaration node, Object data) {
        this.rc = (RuleContext) data;

/*
        IDataFlowNode n1 = node.getDataFlowNode();
        List f = n1.getFlow();
        for (Iterator i = f.iterator(); i.hasNext();) {
            DataFlowNode dfan = (DataFlowNode)i.next();
            System.out.println(dfan);
            List va = dfan.getVariableAccess();
            for (Iterator j = va.iterator(); j.hasNext();) {
                VariableAccess o = (VariableAccess)j.next();
                System.out.println(o);
            }
        }
*/

        DAAPathFinder a = new DAAPathFinder((IDataFlowNode) node.getDataFlowNode().getFlow().get(0), this);
        a.run();

        return data;
    }

    private static class Usage {
        public int accessType;
        public IDataFlowNode node;

        public Usage(int accessType, IDataFlowNode node) {
            this.accessType = accessType;
            this.node = node;
        }

        public String toString() {
            return "accessType = " + accessType + ", line = " + node.getLine();
        }
    }

    public void execute(CurrentPath path) {
        Map hash = new HashMap();
        //System.out.println("path size is " + path.size());
        for (Iterator i = path.iterator(); i.hasNext();) {
            //System.out.println("i = " + i);
            IDataFlowNode inode = (IDataFlowNode) i.next();
            if (inode.getVariableAccess() == null) {
                continue;
            }
            for (int j = 0; j < inode.getVariableAccess().size(); j++) {
                VariableAccess va = (VariableAccess) inode.getVariableAccess().get(j);
                //System.out.println("inode = " + inode + ", va = " + va);
                Object o = hash.get(va.getVariableName());
                if (o != null) {
                    Usage u = (Usage) o;
                    // At some point investigate and possibly reintroduce this line2 thing
                    //int line2 = ((Integer) array.get(1)).intValue();

                    // DD - definition followed by another definition
                    // FIXME need to check for assignment as well!
                    if (va.isDefinition() && va.accessTypeMatches(u.accessType)) {
                        //System.out.println(va.getVariableName() + ":" + u);
                        addViolation(rc, u.node.getSimpleNode(), va.getVariableName());
                    }
/*                        // UR - ??
                  else if (last == VariableAccess.UNDEFINITION && va.isReference()) {
                        //this.rc.getReport().addRuleViolation(createRuleViolation(rc, inode.getSimpleNode(), va.getVariableName(), "UR"));
                    }
                    // DU - variable is defined and then goes out of scope
                    // i.e., unused parameter
                    else if (last == VariableAccess.DEFINITION && va.isUndefinition()) {
                        if (inode.getSimpleNode() != null) {
                            this.rc.getReport().addRuleViolation(createRuleViolation(rc, tmp, va.getVariableName(), "DU"));
                        }
                    }
*/
                }
                Usage u = new Usage(va.getAccessType(), inode);
                hash.put(va.getVariableName(), u);
            }
        }
    }
}
