/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
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
import net.sourceforge.pmd.properties.IntegerProperty;

/**
 * Starts path search for each method and runs code if found.
 *
 * @author raik
 * @author Sven Jacob
 */
public class DataflowAnomalyAnalysisRule extends AbstractJavaRule implements Executable {
    private static final IntegerProperty MAX_PATH_DESCRIPTOR
            = IntegerProperty.named("maxPaths")
                             .desc("Maximum number of checked paths per method. A lower value will increase the performance of the rule but may decrease anomalies found.")
                             .range(100, 8000)
                             .defaultValue(1000)
                             .uiOrder(1.0f).build();
    private static final IntegerProperty MAX_VIOLATIONS_DESCRIPTOR
            = IntegerProperty.named("maxViolations")
                             .desc("Maximum number of anomalies per class")
                             .range(1, 2000)
                             .defaultValue(100)
                             .uiOrder(2.0f).build();
    private RuleContext rc;
    private List<DaaRuleViolation> daaRuleViolations;
    private int maxRuleViolations;
    private int currentRuleViolationCount;


    private static class Usage {
        public int accessType;
        public DataFlowNode node;

        Usage(int accessType, DataFlowNode node) {
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
        daaRuleViolations = new ArrayList<>();

        final DataFlowNode node = methodDeclaration.getDataFlowNode().getFlow().get(0);

        final DAAPathFinder pathFinder = new DAAPathFinder(node, this, getProperty(MAX_PATH_DESCRIPTOR));
        pathFinder.run();

        super.visit(methodDeclaration, data);
        return data;
    }

    public void execute(CurrentPath path) {

        if (maxNumberOfViolationsReached()) {
            return;
        }

        Map<String, Usage> usagesByVarName = new HashMap<>();

        for (DataFlowNode inode : path) {
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

        if (va.accessTypeMatches(u.accessType) && va.isDefinition()) { // DD
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
    private void addDaaViolation(Object data, Node node, String type, String var, int startLine, int endLine) {
        if (!maxNumberOfViolationsReached() && !violationAlreadyExists(type, var, startLine, endLine) && node != null) {
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
     * 
     * @return <code>true</code> if the maximum number of violations was
     *         reached, <code>false</code> otherwise.
     */
    private boolean maxNumberOfViolationsReached() {
        return currentRuleViolationCount >= maxRuleViolations;
    }

    /**
     * Checks if a violation already exists. This is needed because on the
     * different paths same anomalies can occur.
     * 
     * @param type
     * @param var
     * @param startLine
     * @param endLine
     * @return true if the violation already was added to the report
     */
    private boolean violationAlreadyExists(String type, String var, int startLine, int endLine) {
        for (DaaRuleViolation violation : daaRuleViolations) {
            if (violation.getBeginLine() == startLine && violation.getEndLine() == endLine
                    && violation.getType().equals(type) && violation.getVariableName().equals(var)) {
                return true;
            }
        }
        return false;
    }
}
