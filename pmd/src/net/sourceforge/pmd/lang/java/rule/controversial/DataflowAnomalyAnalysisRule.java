/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.controversial;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.dfa.DataFlowNode;
import net.sourceforge.pmd.lang.dfa.VariableAccess;
import net.sourceforge.pmd.lang.dfa.pathfinder.CurrentPath;
import net.sourceforge.pmd.lang.dfa.pathfinder.DAAPathFinder;
import net.sourceforge.pmd.lang.dfa.pathfinder.Executable;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.rule.properties.IntegerProperty;

/**
 * Starts path search for each method and runs code if found.
 *
 * @author raik
 * @author Sven Jacob
 */
public class DataflowAnomalyAnalysisRule extends AbstractJavaRule implements Executable {
    private RuleContext rc;
    private List<DaaRuleViolation> daaRuleViolations;
    private int maxRuleViolations;
    private int currentRuleViolationCount;

    private static final IntegerProperty MAX_PATH_DESCRIPTOR = new IntegerProperty(
            "maxPaths", "Maximum number of checked paths per method. A lower value will increase the performance of the rule but may decrease anomalies found.", 100, 8000, 1000, 1.0f
            );

    private static final IntegerProperty MAX_VIOLATIONS_DESCRIPTOR = new IntegerProperty(
            "maxViolations", "Maximum number of anomalies per class", 1, 2000, 100, 2.0f
            );

    private static class Usage {
        public int accessType;
        public DataFlowNode node;

        public Usage(int accessType, DataFlowNode node) {
            this.accessType = accessType;
            this.node = node;
        }

        public String toString() {
            return "accessType = " + accessType + ", line = " + node.getLine();
        }
    }
    
    public DataflowAnomalyAnalysisRule() {
	definePropertyDescriptor(MAX_PATH_DESCRIPTOR);
	definePropertyDescriptor(MAX_VIOLATIONS_DESCRIPTOR);
    }

    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        maxRuleViolations = getProperty(MAX_VIOLATIONS_DESCRIPTOR);
        currentRuleViolationCount = 0;
        return super.visit(node, data);
    }

    public Object visit(ASTMethodDeclaration methodDeclaration, Object data) {
        rc = (RuleContext) data;
        daaRuleViolations = new ArrayList<DaaRuleViolation>();

        final DataFlowNode node = methodDeclaration.getDataFlowNode().getFlow().get(0);

        final DAAPathFinder pathFinder = new DAAPathFinder(node, this, getProperty(MAX_PATH_DESCRIPTOR));
        pathFinder.run();

        super.visit(methodDeclaration, data);
        return data;
    }

    public void execute(CurrentPath path) {
    	
        if (maxNumberOfViolationsReached()) return;

        Map<String, Usage> usagesByVarName = new HashMap<String, Usage>();

        Iterator<DataFlowNode> pathIterator = path.iterator();
        while (pathIterator.hasNext()) {
            // iterate all nodes in this path
            DataFlowNode inode = pathIterator.next();
            if (inode.getVariableAccess() != null) {
                // iterate all variables of this node
                for (VariableAccess va : inode.getVariableAccess()) {

                    // get the last usage of the current variable
                    Usage lastUsage = usagesByVarName.get(va.getVariableName());
                    if (lastUsage != null) {
                        // there was a usage to this variable before
                        checkVariableAccess(inode, va, lastUsage);
                    }

                    Usage newUsage = new Usage(va.getAccessType(), inode);
                    // put the new usage for the variable
                    usagesByVarName.put(va.getVariableName(), newUsage);
                }
            }
        }
    }

    private void checkVariableAccess(DataFlowNode inode, VariableAccess va, final Usage u) {
        // get the start and end line
        int startLine = u.node.getLine();
        int endLine = inode.getLine();

        Node lastNode = inode.getNode();
        Node firstNode = u.node.getNode();

        if (va.accessTypeMatches(u.accessType) && va.isDefinition() ) { // DD
            addDaaViolation(rc, lastNode, "DD", va.getVariableName(), startLine, endLine);
        } else if (u.accessType == VariableAccess.UNDEFINITION && va.isReference()) { // UR
            addDaaViolation(rc, lastNode, "UR", va.getVariableName(), startLine, endLine);
        } else if (u.accessType == VariableAccess.DEFINITION && va.isUndefinition()) { // DU
            addDaaViolation(rc, firstNode, "DU", va.getVariableName(), startLine, endLine);
        }
    }

    /**
     * Adds a daa violation to the report.
     */
    private final void addDaaViolation(Object data, Node node, String type, String var, int startLine, int endLine) {
        if (!maxNumberOfViolationsReached()
                && !violationAlreadyExists(type, var, startLine, endLine)
                && node != null) {
            RuleContext ctx = (RuleContext) data;
            String msg = type;
            if (getMessage() != null) {
                msg = MessageFormat.format(getMessage(), type, var, startLine, endLine);
            }
            DaaRuleViolation violation = new DaaRuleViolation(this, ctx, node, type, msg, var, startLine, endLine);
            ctx.getReport().addRuleViolation(violation);
            daaRuleViolations.add(violation);
            currentRuleViolationCount++;
      }
    }

    /**
     * Maximum number of violations was already reached?
     * @return <code>true</code> if the maximum number of violations was reached,
     * <code>false</code> otherwise.
     */
    private boolean maxNumberOfViolationsReached() {
        return currentRuleViolationCount >= maxRuleViolations;
    }

    /**
     * Checks if a violation already exists.
     * This is needed because on the different paths same anomalies can occur.
     * @param type
     * @param var
     * @param startLine
     * @param endLine
     * @return true if the violation already was added to the report
     */
    private boolean violationAlreadyExists(String type, String var, int startLine, int endLine) {
        for(DaaRuleViolation violation: daaRuleViolations) {
            if ((violation.getBeginLine() == startLine)
                    && (violation.getEndLine() == endLine)
                    && violation.getType().equals(type)
                    && violation.getVariableName().equals(var)) {
                return true;
            }
        }
        return false;
    }
}
