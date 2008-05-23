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

import net.sourceforge.pmd.PropertyDescriptor;
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
    private RuleContext rc;
    private List<DaaRuleViolation> daaRuleViolations;
    private int maxRuleViolations;
    private int currentRuleViolationCount;

    private static final PropertyDescriptor maxPathDescriptor = new IntegerProperty(
            "maxpaths", "Maximum number of paths per method", 5000, 1.0f
            );

    private static final PropertyDescriptor maxViolationsDescriptor = new IntegerProperty(
            "maxviolations", "Maximum number of anomalys per class", 1000, 2.0f
            );

    private static final Map<String, PropertyDescriptor> propertyDescriptorsByName = asFixedMap(
            new PropertyDescriptor[] { maxPathDescriptor, maxViolationsDescriptor});

    protected Map<String, PropertyDescriptor> propertiesByName() {
        return propertyDescriptorsByName;
    }

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

    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        this.maxRuleViolations = getIntProperty(maxViolationsDescriptor);
        this.currentRuleViolationCount = 0;
        return super.visit(node, data);
    }

    public Object visit(ASTMethodDeclaration methodDeclaration, Object data) {
        this.rc = (RuleContext) data;
        this.daaRuleViolations = new ArrayList<DaaRuleViolation>();

        final DataFlowNode node = methodDeclaration.getDataFlowNode().getFlow().get(0);

        final DAAPathFinder pathFinder = new DAAPathFinder(node, this, getIntProperty(maxPathDescriptor));
        pathFinder.run();

        super.visit(methodDeclaration, data);
        return data;
    }

    public void execute(CurrentPath path) {
        if (maxNumberOfViolationsReached()) {
            // dont execute this path if the limit is already reached
            return;
        }

        final Map<String, Usage> hash = new HashMap<String, Usage>();

        final Iterator<DataFlowNode> pathIterator = path.iterator();
        while (pathIterator.hasNext()) {
            // iterate all nodes in this path
            DataFlowNode inode = pathIterator.next();
            if (inode.getVariableAccess() != null) {
                // iterate all variables of this node
                for (int g = 0; g < inode.getVariableAccess().size(); g++) {
                    final VariableAccess va = inode.getVariableAccess().get(g);

                    // get the last usage of the current variable
                    final Usage lastUsage = hash.get(va.getVariableName());
                    if (lastUsage != null) {
                        // there was a usage to this variable before
                        checkVariableAccess(inode, va, lastUsage);
                    }

                    final Usage newUsage = new Usage(va.getAccessType(), inode);
                    // put the new usage for the variable
                    hash.put(va.getVariableName(), newUsage);
                }
            }
        }
    }

    /**
     * @param inode
     * @param va
     * @param o
     */
    private void checkVariableAccess(DataFlowNode inode, VariableAccess va, final Usage u) {
        // get the start and end line
        final int startLine = u.node.getLine();
        final int endLine = inode.getLine();

        final Node lastNode = inode.getNode();
        final Node firstNode = u.node.getNode();

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
     *
     * @param ctx  the RuleContext
     * @param node the node that produces the violation
     * @param msg  specific message to put in the report
     */
    private final void addDaaViolation(Object data, Node node, String type, String var, int startLine, int endLine) {
        if (!maxNumberOfViolationsReached()
                && !violationAlreadyExists(type, var, startLine, endLine)
                && node != null) {
            final RuleContext ctx = (RuleContext) data;
            String msg = type;
            if (getMessage() != null) {
                msg = MessageFormat.format(getMessage(), type, var, startLine, endLine);
            }
            final DaaRuleViolation violation = new DaaRuleViolation(this, ctx, node, type, msg, var, startLine, endLine);
            ctx.getReport().addRuleViolation(violation);
            this.daaRuleViolations.add(violation);
            this.currentRuleViolationCount++;
      }
    }

    /**
     * Maximum number of violations was already reached?
     * @return
     */
    private boolean maxNumberOfViolationsReached() {
        return this.currentRuleViolationCount >= this.maxRuleViolations;
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
        for(DaaRuleViolation violation: this.daaRuleViolations) {
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
