/*
 * Created on 20.07.2004
 */
package net.sourceforge.pmd.dfa;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.dfa.pathfinder.CurrentPath;
import net.sourceforge.pmd.dfa.pathfinder.DAAPathFinder;
import net.sourceforge.pmd.dfa.pathfinder.Executable;
import net.sourceforge.pmd.dfa.variableaccess.VariableAccess;
import net.sourceforge.pmd.properties.IntegerProperty;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Starts path search for each method and runs code if found.
 * 
 * @author raik
 * @author Sven Jacob
 */
public class DaaRule extends AbstractRule implements Executable {
    private RuleContext rc;
    private List daaRuleViolations;
    private int maxRuleViolations;
    private int currentRuleViolationCount;
   
    private static final PropertyDescriptor maxPathDescriptor = new IntegerProperty(
            "maxpaths", "Maximum number of paths per method", 5000, 1.0f
            );

    private static final PropertyDescriptor maxViolationsDescriptor = new IntegerProperty(
            "maxviolations", "Maximum number of anomalys per class", 1000, 2.0f
            );
        
    private static final Map propertyDescriptorsByName = asFixedMap(
            new PropertyDescriptor[] { maxPathDescriptor, maxViolationsDescriptor});
            
    protected Map propertiesByName() {
        return propertyDescriptorsByName;
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
    
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        this.maxRuleViolations = getIntProperty(maxViolationsDescriptor);
        this.currentRuleViolationCount = 0;
        return super.visit(node, data);
    }
    
    public Object visit(ASTMethodDeclaration methodDeclaration, Object data) {
        this.rc = (RuleContext) data;
        this.daaRuleViolations = new ArrayList();
        
        final IDataFlowNode node = (IDataFlowNode) methodDeclaration.getDataFlowNode().getFlow().get(0);
        
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
        
        final Hashtable hash = new Hashtable();
        
        final Iterator pathIterator = path.iterator();
        while (pathIterator.hasNext()) {
            // iterate all nodes in this path
            IDataFlowNode inode = (IDataFlowNode) pathIterator.next();
            if (inode.getVariableAccess() != null) {
                // iterate all variables of this node
                for (int g = 0; g < inode.getVariableAccess().size(); g++) {
                    final VariableAccess va = (VariableAccess) inode.getVariableAccess().get(g);
                    
                    // get the last usage of the current variable
                    final Usage lastUsage = (Usage) hash.get(va.getVariableName());
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
    private void checkVariableAccess(IDataFlowNode inode, VariableAccess va, final Usage u) {
        // get the start and end line
        final int startLine = u.node.getLine();
        final int endLine = inode.getLine();
        
        final SimpleNode lastNode = inode.getSimpleNode();
        final SimpleNode firstNode = u.node.getSimpleNode();
        
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
    private final void addDaaViolation(Object data, SimpleNode node, String type, String var, int startLine, int endLine) {
        if (!maxNumberOfViolationsReached() 
                && !violationAlreadyExists(type, var, startLine, endLine)
                && node != null) {
            final RuleContext ctx = (RuleContext) data;
            final Object[] params = new Object[] { type, var, new Integer(startLine), new Integer(endLine) };
            String msg = type;
            if (getMessage() != null) {
                msg = MessageFormat.format(getMessage(), params);
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
        final Iterator violationIterator = this.daaRuleViolations.iterator();
        while (violationIterator.hasNext()) {
            final DaaRuleViolation violation = (DaaRuleViolation)violationIterator.next();
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
