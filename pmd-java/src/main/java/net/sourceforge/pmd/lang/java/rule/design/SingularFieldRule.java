/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.design;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAssignmentOperator;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTInitializer;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTStatementExpression;
import net.sourceforge.pmd.lang.java.ast.ASTSynchronizedStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;
import net.sourceforge.pmd.lang.rule.properties.BooleanProperty;

/**
 * @author Eric Olander
 * @author Wouter Zelle
 * @since Created on April 17, 2005, 9:49 PM
 */
public class SingularFieldRule extends AbstractJavaRule {

	/**
	 * Restore old behavior by setting both properties to true, which will result in many false positives
	 */
    private static final BooleanProperty CHECK_INNER_CLASSES = new BooleanProperty(
			"checkInnerClasses", "Check inner classes", false, 1.0f);
    private static final BooleanProperty DISALLOW_NOT_ASSIGNMENT = new BooleanProperty(
			"disallowNotAssignment", "Disallow violations where the first usage is not an assignment", false, 2.0f);

    public SingularFieldRule() {
	definePropertyDescriptor(CHECK_INNER_CLASSES);
	definePropertyDescriptor(DISALLOW_NOT_ASSIGNMENT);
    }
    
    @SuppressWarnings("PMD.CompareObjectsWithEquals")
    @Override
    public Object visit(ASTFieldDeclaration node, Object data) {
    	boolean checkInnerClasses = getProperty(CHECK_INNER_CLASSES);
    	boolean disallowNotAssignment = getProperty(DISALLOW_NOT_ASSIGNMENT);

        if (node.isPrivate() && !node.isStatic()) {
            for (ASTVariableDeclarator declarator: node.findChildrenOfType(ASTVariableDeclarator.class)) {
        	ASTVariableDeclaratorId declaration = (ASTVariableDeclaratorId) declarator.jjtGetChild(0);
                List<NameOccurrence> usages = declaration.getUsages();
                Node decl = null;
                boolean violation = true;
                for (int ix = 0; ix < usages.size(); ix++) {
                    NameOccurrence no = usages.get(ix);
                    Node location = no.getLocation();

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
    	                	    Node secondUsageLocation = usages.get(ix + 1).getLocation();

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

                    Node method = location.getFirstParentOfType(ASTMethodDeclaration.class);
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
                    } else if (decl != method
                            // handle inner classes
                            && decl.getFirstParentOfType(ASTClassOrInterfaceDeclaration.class)
                                == method.getFirstParentOfType(ASTClassOrInterfaceDeclaration.class)) {
                        violation = false;
                        break;			//Optimization
                    }


                }

                if (violation && !usages.isEmpty()) {
                    addViolation(data, node, new Object[] { declaration.getImage() });
                }
            }
        }
        return data;
    }

	private boolean isInAssignment(Node potentialStatement) {
		if (potentialStatement instanceof ASTStatementExpression) {
			ASTStatementExpression statement = (ASTStatementExpression)potentialStatement;
			List<ASTAssignmentOperator> assignments = new ArrayList<ASTAssignmentOperator>();
			statement.findDescendantsOfType(ASTAssignmentOperator.class, assignments, false);
			return !assignments.isEmpty() && "=".equals(assignments.get(0).getImage());
		} else {
			return false;
		}
	}
}
