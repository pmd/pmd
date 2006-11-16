/*
 * Created on 20.07.2004
 */
package net.sourceforge.pmd.dfa;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.dfa.pathfinder.CurrentPath;
import net.sourceforge.pmd.dfa.pathfinder.DAAPathFinder;
import net.sourceforge.pmd.dfa.pathfinder.Executable;
import net.sourceforge.pmd.dfa.variableaccess.VariableAccess;

import java.text.MessageFormat;
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
    private List daaRuleViolations;
    private final static String PROPERTY_MAX_PATH = "maxpaths";
    private final static String PROPERTY_MAX_VIOLATIONS = "maxviolations";
    private final static int DEFAULT_MAX_VIOLATIONS = 1000;
    private int maxRuleViolations;
    private int currentRuleViolationCount;
    
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (this.hasProperty(PROPERTY_MAX_VIOLATIONS)) {
            this.maxRuleViolations = this.getIntProperty(PROPERTY_MAX_VIOLATIONS);
        } else {
            this.maxRuleViolations = DEFAULT_MAX_VIOLATIONS;
        }     
        
        this.currentRuleViolationCount = 0;
        return super.visit(node, data);
    }
    
    public Object visit(ASTMethodDeclaration node, Object data) {
        this.rc = (RuleContext) data;
        this.daaRuleViolations = new ArrayList();
        
        IDataFlowNode n = (IDataFlowNode) node.getDataFlowNode().getFlow().get(0);
        
        DAAPathFinder a;
        if (this.hasProperty(PROPERTY_MAX_PATH)) {
            a = new DAAPathFinder(n, this, this.getIntProperty(PROPERTY_MAX_PATH));
        } else {
            a = new DAAPathFinder(n, this);
        }
        
        a.run();

        super.visit(node, data);
        return data;
    }

    public void execute(CurrentPath path) {
        if (maxNumberOfViolationsReached()) {
            // dont execute this path if the limit is already reached
            return;
        }
        
        Hashtable hash = new Hashtable();
        
        for (Iterator d = path.iterator(); d.hasNext();) {
            IDataFlowNode inode = (IDataFlowNode) d.next();
            if (inode.getVariableAccess() != null) {
                for (int g = 0; g < inode.getVariableAccess().size(); g++) {
                    VariableAccess va = (VariableAccess) inode.getVariableAccess().get(g);

                    Object o = hash.get(va.getVariableName());
                    if (o != null) {
                        List array = (List) o;
                        // get the last access type
                        int lastAccessType = ((Integer) array.get(0)).intValue();
                        // get the start and end line
                        int startLine = ((Integer) array.get(2)).intValue();
                        int endLine = inode.getLine();
                        
                        if (va.accessTypeMatches(lastAccessType) && va.isDefinition()) { // DD
                            if (inode.getSimpleNode() != null) {
                                // preventing NullpointerException
                                addDaaViolation(rc, inode.getSimpleNode(), "DD", va.getVariableName(), startLine, endLine);
                            }
                        } else if (lastAccessType == VariableAccess.UNDEFINITION && va.isReference()) { // UR
                            if (inode.getSimpleNode() != null) {
                                // preventing NullpointerException                        		
                                addDaaViolation(rc, inode.getSimpleNode(), "UR", va.getVariableName(), startLine, endLine);
                            }
                        } else if (lastAccessType == VariableAccess.DEFINITION && va.isUndefinition()) { // DU
                            if (inode.getSimpleNode() != null) {
                                addDaaViolation(rc, inode.getSimpleNode(), "DU", va.getVariableName(), startLine, endLine);
                            } else {
                        	// undefinition outside, get the node of the definition
                                SimpleNode lastSimpleNode = (SimpleNode)array.get(1);
                                if (lastSimpleNode != null) {
                                    addDaaViolation(rc, lastSimpleNode, "DU", va.getVariableName(), startLine, endLine);
                                }
                            }
                        }
                    }
                    List array = new ArrayList();
                    array.add(new Integer(va.getAccessType()));
                    array.add(inode.getSimpleNode());
                    array.add(new Integer(inode.getLine()));
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
    private final void addDaaViolation(Object data, SimpleNode node, String type, String var, int startLine, int endLine) {
        if (!maxNumberOfViolationsReached() 
                && !violationAlreadyExists(type, var, startLine, endLine)) {
            RuleContext ctx = (RuleContext) data;
            Object[] params = new Object[] { type, var, new Integer(startLine), new Integer(endLine) };
            String msg = type;
            if (getMessage() != null) {
                msg = MessageFormat.format(getMessage(), params);
            }
            DaaRuleViolation violation = new DaaRuleViolation(this, ctx, node, type, msg, var, startLine, endLine);
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
        Iterator violationIterator = this.daaRuleViolations.iterator();
        while (violationIterator.hasNext()) {
            DaaRuleViolation violation = (DaaRuleViolation)violationIterator.next();
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
