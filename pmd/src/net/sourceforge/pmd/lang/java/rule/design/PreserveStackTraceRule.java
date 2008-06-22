/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.design;

import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTCastExpression;
import net.sourceforge.pmd.lang.java.ast.ASTCatchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTThrowStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.NameOccurrence;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;

import org.jaxen.JaxenException;

/**
 *
 * @author Unknown,
 * @author Romain PELISSE, belaran@gmail.com, fix for bug 1808110
 *
 */
public class PreserveStackTraceRule extends AbstractJavaRule {

    // FUTURE: This detection is name based, it should probably use Type Resolution, to become type "based"
    // it assumes the exception class contains 'Exception' in its name
    private static final String FIND_THROWABLE_INSTANCE =
	"./VariableInitializer/Expression/PrimaryExpression/PrimaryPrefix/AllocationExpression" +
	"[ClassOrInterfaceType[contains(@Image,'Exception')] and Arguments[count(*)=0]]";

    private static final String ILLEGAL_STATE_EXCEPTION = "IllegalStateException";
    private static final String FILL_IN_STACKTRACE = ".fillInStackTrace";

    @Override
    public Object visit(ASTCatchStatement catchStmt, Object data) {
        String target = catchStmt.jjtGetChild(0).jjtGetChild(1).getImage();
        // Inspect all the throw stmt inside the catch stmt
        List<ASTThrowStatement> lstThrowStatements = catchStmt.findDescendantsOfType(ASTThrowStatement.class);
        for (ASTThrowStatement throwStatement : lstThrowStatements) {
            Node n = throwStatement.jjtGetChild(0).jjtGetChild(0);
            if (n.getClass().equals(ASTCastExpression.class)) {
                ASTPrimaryExpression expr = (ASTPrimaryExpression) n.jjtGetChild(1);
                if (expr.jjtGetNumChildren() > 1 && expr.jjtGetChild(1).getClass().equals(ASTPrimaryPrefix.class)) {
                    RuleContext ctx = (RuleContext) data;
                    addViolation(ctx, throwStatement);
                }
                continue;
            }
            // If the thrown exception is IllegalStateException, no way to preserve the exception (the constructor has no args)
            if ( ! isThrownExceptionOfType(throwStatement,ILLEGAL_STATE_EXCEPTION) ) {
	            // Retrieve all argument for the throw exception (to see if the original exception is preserved)
	            ASTArgumentList args = throwStatement.getFirstDescendantOfType(ASTArgumentList.class);

	            if (args != null) {
	                ck(data, target, throwStatement, args);
	            }
	            else {
	        	Node child = throwStatement.jjtGetChild(0);
	                while (child != null && child.jjtGetNumChildren() > 0
	                        && !child.getClass().equals(ASTName.class)) {
	                    child = child.jjtGetChild(0);
	                }
	                if (child != null){
	                    if( child.getClass().equals(ASTName.class) && !target.equals(child.getImage()) && !child.hasImageEqualTo(target + FILL_IN_STACKTRACE)) {
	                        Map<VariableNameDeclaration, List<NameOccurrence>> vars = ((ASTName) child).getScope().getVariableDeclarations();
		                    for (VariableNameDeclaration decl: vars.keySet()) {
		                        args = decl.getNode().jjtGetParent()
		                                .getFirstDescendantOfType(ASTArgumentList.class);
		                        if (args != null) {
		                            ck(data, target, throwStatement, args);
		                        }
		                    }
	                    } else if(child.getClass().equals(ASTClassOrInterfaceType.class)){
	                       addViolation(data, throwStatement);
	                    }
	                }
	            }
            }

        }
        return super.visit(catchStmt, data);
    }

    @Override
    public Object visit(ASTVariableDeclarator node, Object data) {
	// Search Catch stmt nodes for variable used to store improperly created throwable or exception
	try {
	    List<Node> nodes = node.findChildNodesWithXPath(FIND_THROWABLE_INSTANCE);
	    if (nodes.size() > 0) {
		String variableName = node.jjtGetChild(0).getImage(); // VariableDeclatorId
		ASTCatchStatement catchStmt = node.getFirstParentOfType(ASTCatchStatement.class);

		while (catchStmt != null) {
		    List<Node> violations = catchStmt.findChildNodesWithXPath("//Expression/PrimaryExpression/PrimaryPrefix/Name[@Image = '" + variableName + "']");
		    if (violations != null && violations.size() > 0) {
			// If, after this allocation, the 'initCause' method is called, and the ex passed
			// this is not a violation
			if (!useInitCause(violations.get(0), catchStmt)) {
			    addViolation(data, node);
			}
		    }

		    // check ASTCatchStatement higher up
		    catchStmt = catchStmt.getFirstParentOfType(ASTCatchStatement.class);
		}
	    }
	    return super.visit(node, data);
	} catch (JaxenException e) {
	    // XPath is valid, this should never happens...
	    throw new IllegalStateException(e);
	}
    }

	private boolean useInitCause(Node node, ASTCatchStatement catchStmt) throws JaxenException {
		// In case of NPE...
		if ( node != null && node.getImage() != null )
		{
			List <Node> nodes = catchStmt.findChildNodesWithXPath("descendant::StatementExpression/PrimaryExpression/PrimaryPrefix/Name[@Image = '" + node.getImage() + ".initCause']");
			if ( nodes != null && nodes.size() > 0 )
			{
				return true;
			}
		}
		return false;
	}

	private boolean isThrownExceptionOfType(ASTThrowStatement throwStatement,String type) {
    	boolean status = false;
    	try {
			List<Node> results = throwStatement.findChildNodesWithXPath("Expression/PrimaryExpression/PrimaryPrefix/AllocationExpression/ClassOrInterfaceType[@Image = '" + type + "']");
			// If we have a match, return true
			if ( results != null && results.size() == 1 ) {
				status = true;
			}
		} catch (JaxenException e) {
			// XPath is valid, this should never happens !
			e.printStackTrace();
		}
    	return status;
	}

	private void ck(Object data, String target, ASTThrowStatement throwStatement,
                    ASTArgumentList args) {
        boolean match = false;
        List<ASTName> nameNodes = args.findDescendantsOfType(ASTName.class);
        for (ASTName nameNode : nameNodes) {
            if (target.equals(nameNode.getImage())) {
                match = true;
                break;
            }
        }
        if ( ! match) {
            RuleContext ctx = (RuleContext) data;
            addViolation(ctx, throwStatement);
        }
    }
}
