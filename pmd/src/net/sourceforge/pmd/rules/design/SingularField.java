/*
 * SingularField.java
 *
 * Created on April 17, 2005, 9:49 PM
 */
package net.sourceforge.pmd.rules.design;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.PropertyDescriptor;
import net.sourceforge.pmd.ast.ASTAssignmentOperator;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.ast.ASTIfStatement;
import net.sourceforge.pmd.ast.ASTInitializer;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.ast.ASTStatementExpression;
import net.sourceforge.pmd.ast.ASTSynchronizedStatement;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.ast.Node;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.properties.BooleanProperty;
import net.sourceforge.pmd.symboltable.NameOccurrence;

/**
 * @author Eric Olander
 * @author Wouter Zelle
 */
public class SingularField extends AbstractRule {
	
	/**
	 * Restore old behaviour by setting both properties to true, which will result in many false positives
	 */
    private static final PropertyDescriptor CHECK_INNER_CLASSES = new BooleanProperty(
			"CheckInnerClasses", "Check inner classes", false, 1.0f);
    private static final PropertyDescriptor DISALLOW_NOT_ASSIGNMENT = new BooleanProperty(
			"DisallowNotAssignment", "Disallow violations where the first usage is not an assignment", false, 1.0f);
	
    public Object visit(ASTFieldDeclaration node, Object data) {
    	boolean checkInnerClasses = getBooleanProperty(CHECK_INNER_CLASSES);
    	boolean disallowNotAssignment = getBooleanProperty(DISALLOW_NOT_ASSIGNMENT);
    	
        if (node.isPrivate() && !node.isStatic()) {
            List<ASTVariableDeclaratorId> list = node.findChildrenOfType(ASTVariableDeclaratorId.class);
            ASTVariableDeclaratorId declaration = list.get(0);
            List<NameOccurrence> usages = declaration.getUsages();
            SimpleNode decl = null;
            boolean violation = true;
            for (int ix = 0; ix < usages.size(); ix++) {
                NameOccurrence no = usages.get(ix);
                SimpleNode location = no.getLocation();

                ASTPrimaryExpression primaryExpressionParent = location.getFirstParentOfType(ASTPrimaryExpression.class);
                if (ix==0 && !disallowNotAssignment) {
                	if (primaryExpressionParent.getFirstParentOfType(ASTIfStatement.class) != null) {
                		//the first usage is in an if, so it may be skipped on 
                		//later calls to the method. So this might be legit code
                		//that simply stores an object for later use.
                		violation = false;
	                	break;		//Optimization
                	}
                	
                	//Is the first usage in an assignment?
                	Node potentialStatement = primaryExpressionParent.jjtGetParent();
	                boolean assignmentToField = no.getImage().equals(location.getImage());	//Check the the assignment is not to a field inside the field object
					if (!assignmentToField || !isInAssignment(potentialStatement)) {
	                	violation = false;
	                	break;		//Optimization
	                } else {
	                	if (usages.size() > ix + 1) {
	                		SimpleNode secondUsageLocation = usages.get(ix + 1).getLocation();
	                		
	                		List<ASTStatementExpression> parentStatements = secondUsageLocation.getParentsOfType(ASTStatementExpression.class);
	                		for (ASTStatementExpression statementExpression : parentStatements) {
	                			if (statementExpression != null && statementExpression.equals(potentialStatement)) {
		                			//The second usage is in the assignment of the first usage, which is allowed
		                			violation = false;
		    	                	break;		//Optimization
		                		}
							}
	                		
	                	}
	                }
                }
                
                if (!checkInnerClasses) {
	                //Skip inner classes because the field can be used in the outer class and checking this is too difficult
	                ASTClassOrInterfaceDeclaration clazz = location.getFirstParentOfType(ASTClassOrInterfaceDeclaration.class);
	                if (clazz!= null && clazz.getFirstParentOfType(ASTClassOrInterfaceDeclaration.class) != null) {
	                	violation = false;
	                	break;			//Optimization
	                }
                }
                
                if (primaryExpressionParent.jjtGetParent() instanceof ASTSynchronizedStatement) {
                	//This usage is directly in an expression of a synchronized block
                	violation = false;
                	break;			//Optimization
                }
                
                SimpleNode method = location.getFirstParentOfType(ASTMethodDeclaration.class);
                if (method == null) {
                    method = location.getFirstParentOfType(ASTConstructorDeclaration.class);
                    if (method == null) {
                    	method = location.getFirstParentOfType(ASTInitializer.class);
                    	if (method == null) {
                    		continue;
                    	}
                    }
                }

                if (decl == null) {
                    decl = method;
                    continue;
                } else if (decl != method) {
                    violation = false;
                    break;			//Optimization
                }
                
                
            }

            if (violation && !usages.isEmpty()) {
                addViolation(data, node, new Object[] { declaration.getImage() });
            }
        }
        return data;
    }

	private boolean isInAssignment(Node potentialStatement) {
		if (potentialStatement instanceof ASTStatementExpression) {
			ASTStatementExpression statement = (ASTStatementExpression)potentialStatement;
			List<ASTAssignmentOperator> assignments = new ArrayList<ASTAssignmentOperator>();
			statement.findChildrenOfType(ASTAssignmentOperator.class, assignments, false);
			if (assignments.isEmpty() || !"=".equals(assignments.get(0).getImage())) {
				return false;
			} else {
				return true;
			}
		} else {
			return false;
		}
	}
}
