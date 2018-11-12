/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.dfa.DataFlowNode;
import net.sourceforge.pmd.lang.dfa.VariableAccess;
import net.sourceforge.pmd.lang.dfa.pathfinder.CurrentPath;
import net.sourceforge.pmd.lang.dfa.pathfinder.DAAPathFinder;
import net.sourceforge.pmd.lang.dfa.pathfinder.Executable;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;

//FUTURE This is not referenced by any RuleSet?
public class UselessAssignment extends AbstractJavaRule implements Executable {

    private RuleContext rc;

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        this.rc = (RuleContext) data;

        /*
         * IDataFlowNode n1 = node.getDataFlowNode(); List f = n1.getFlow(); for
         * (Iterator i = f.iterator(); i.hasNext();) { DataFlowNode dfan =
         * (DataFlowNode)i.next(); System.out.println(dfan); List va =
         * dfan.getVariableAccess(); for (Iterator j = va.iterator();
         * j.hasNext();) { VariableAccess o = (VariableAccess)j.next();
         * System.out.println(o); } }
         */

        DAAPathFinder a = new DAAPathFinder(node.getDataFlowNode().getFlow().get(0), this);
        a.run();

        return data;
    }

    private static class Usage {
        public int accessType;
        public DataFlowNode node;

        Usage(int accessType, DataFlowNode node) {
            this.accessType = accessType;
            this.node = node;
        }

        @Override
        public String toString() {
            return "accessType = " + accessType + ", line = " + node.getLine();
        }
    }

    @Override
    public void execute(CurrentPath path) {
        Map<String, Usage> hash = new HashMap<>();
        // System.out.println("path size is " + path.size());
        for (Iterator<DataFlowNode> i = path.iterator(); i.hasNext();) {
            // System.out.println("i = " + i);
            DataFlowNode inode = i.next();
            if (inode.getVariableAccess() == null) {
                continue;
            }
            for (int j = 0; j < inode.getVariableAccess().size(); j++) {
                VariableAccess va = inode.getVariableAccess().get(j);
                // System.out.println("inode = " + inode + ", va = " + va);
                Usage u = hash.get(va.getVariableName());
                if (u != null) {
                    // At some point investigate and possibly reintroduce this
                    // line2 thing
                    // int line2 = ((Integer) array.get(1)).intValue();

                    // DD - definition followed by another definition
                    // FIXME need to check for assignment as well!
                    if (va.isDefinition() && va.accessTypeMatches(u.accessType)) {
                        // System.out.println(va.getVariableName() + ":" + u);
                        addViolation(rc, u.node.getNode(), va.getVariableName());
                    }
                    /*
                     * // UR - ?? else if (last == VariableAccess.UNDEFINITION
                     * && va.isReference()) {
                     * //this.rc.getReport().addRuleViolation(
                     * createRuleViolation(rc, inode.getNode(),
                     * va.getVariableName(), "UR")); } // DU - variable is
                     * defined and then goes out of scope // i.e., unused
                     * parameter else if (last == VariableAccess.DEFINITION &&
                     * va.isUndefinition()) { if (inode.getNode() != null) {
                     * this.rc.getReport().addRuleViolation(createRuleViolation(
                     * rc, tmp, va.getVariableName(), "DU")); } }
                     */
                }
                u = new Usage(va.getAccessType(), inode);
                hash.put(va.getVariableName(), u);
            }
        }
    }
}
