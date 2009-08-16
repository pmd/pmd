/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.basic;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAssignmentOperator;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTNullLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTReferenceType;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTStatementExpression;
import net.sourceforge.pmd.lang.java.ast.ASTSynchronizedStatement;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

/**
 * void method() {
 * if(x == null) {
 * synchronized(this){
 * if(x == null) {
 * x = new | method();
 * }
 * }
 * }
 * 1.  The error is when one uses the value assigned within a synchronized
 * section, outside of a synchronized section.
 * if(x == null) is outside of synchronized section
 * x = new | method();
 * <p/>
 * <p/>
 * Very very specific check for double checked locking.
 *
 * @author CL Gilbert (dnoyeb@users.sourceforge.net)
 */
public class DoubleCheckedLockingRule extends AbstractJavaRule {

    private List<String> volatileFields;

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (node.isInterface()) {
            return data;
        }
        return super.visit(node, data);
    }
 
    @Override
    public Object visit(ASTCompilationUnit compilationUnit, Object data) {
        if ( this.volatileFields == null ) {
            this.volatileFields = new ArrayList<String>(0);
	} else {
	    this.volatileFields.clear();
	}
	return super.visit(compilationUnit,data);
    }


    @Override
    public Object visit(ASTFieldDeclaration fieldDeclaration, Object data) {
        if ( fieldDeclaration.isVolatile() ) {
        	for (ASTVariableDeclaratorId declarator : fieldDeclaration.findDescendantsOfType(ASTVariableDeclaratorId.class) ) {
                this.volatileFields.add(declarator.getImage());
        	}
        }
        return super.visit(fieldDeclaration, data);
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        if (node.getResultType().isVoid()) {
            return super.visit(node, data);
        }

        ASTType typeNode = (ASTType) node.getResultType().jjtGetChild(0);
        if (typeNode.jjtGetNumChildren() == 0 || !(typeNode.jjtGetChild(0) instanceof ASTReferenceType)) {
            return super.visit(node, data);
        }

        List<ASTReturnStatement> rsl = node.findDescendantsOfType(ASTReturnStatement.class);
        if (rsl.size() != 1) {
            return super.visit(node, data);
        }
        ASTReturnStatement rs = rsl.get(0);

        List<ASTPrimaryExpression> pel = rs.findDescendantsOfType(ASTPrimaryExpression.class);
        ASTPrimaryExpression ape = pel.get(0);
        Node lastChild = ape.jjtGetChild(ape.jjtGetNumChildren() - 1);
        String returnVariableName = null;
        if (lastChild instanceof ASTPrimaryPrefix) {
            returnVariableName = getNameFromPrimaryPrefix((ASTPrimaryPrefix) lastChild);
        }
        // With Java5 and volatile keyword, DCL is no longer an issue
        if (returnVariableName == null || this.volatileFields.contains(returnVariableName)) {
            return super.visit(node, data);
        }
        List<ASTIfStatement> isl = node.findDescendantsOfType(ASTIfStatement.class);
        if (isl.size() == 2) {
            ASTIfStatement is = isl.get(0);
            if (ifVerify(is, returnVariableName)) {
                //find synchronized
                List<ASTSynchronizedStatement> ssl = is.findDescendantsOfType(ASTSynchronizedStatement.class);
                if (ssl.size() == 1) {
                    ASTSynchronizedStatement ss = ssl.get(0);
                    isl = ss.findDescendantsOfType(ASTIfStatement.class);
                    if (isl.size() == 1) {
                        ASTIfStatement is2 = isl.get(0);
                        if (ifVerify(is2, returnVariableName)) {
                            List<ASTStatementExpression> sel = is2.findDescendantsOfType(ASTStatementExpression.class);
                            if (sel.size() == 1) {
                                ASTStatementExpression se = sel.get(0);
                                if (se.jjtGetNumChildren() == 3) { //primaryExpression, AssignmentOperator, Expression
                                    if (se.jjtGetChild(0) instanceof ASTPrimaryExpression) {
                                        ASTPrimaryExpression pe = (ASTPrimaryExpression) se.jjtGetChild(0);
                                        if (matchName(pe, returnVariableName)) {
                                            if (se.jjtGetChild(1) instanceof ASTAssignmentOperator) {
                                                addViolation(data, node);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        return super.visit(node, data);
    }

    private boolean ifVerify(ASTIfStatement is, String varname) {
        List<ASTPrimaryExpression> finder = is.findDescendantsOfType(ASTPrimaryExpression.class);
        if (finder.size() > 1) { 
            ASTPrimaryExpression nullStmt = findNonVariableStmt(varname,finder.get(0),finder.get(1));
            if ( nullStmt != null ) {
                if ((nullStmt.jjtGetNumChildren() == 1) && (nullStmt.jjtGetChild(0) instanceof ASTPrimaryPrefix)) {
                    ASTPrimaryPrefix pp2 = (ASTPrimaryPrefix) nullStmt.jjtGetChild(0);
                    if ((pp2.jjtGetNumChildren() == 1) && (pp2.jjtGetChild(0) instanceof ASTLiteral)) {
                        ASTLiteral lit = (ASTLiteral) pp2.jjtGetChild(0);
                        if ((lit.jjtGetNumChildren() == 1) && (lit.jjtGetChild(0) instanceof ASTNullLiteral)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

    /** 
     * <p>Sort out if apeLeft or apeRight are variable with the provided 'variableName'.</p>
     * 
     * @param variableName
     * @param apeLeft
     * @param apeRight
     * @return reference from either apeLeft or apeRight, if one of them match, or 'null', if none match.
     */
	private ASTPrimaryExpression findNonVariableStmt(String variableName,
			ASTPrimaryExpression apeLeft, ASTPrimaryExpression apeRight) {
    	if (matchName(apeLeft, variableName) ) {
    		return apeRight;
    	}
    	else if (matchName(apeRight, variableName) ) {
    		return apeLeft;
    	}
		return null;
	}

	private boolean matchName(ASTPrimaryExpression ape, String name) {
        if ((ape.jjtGetNumChildren() == 1) && (ape.jjtGetChild(0) instanceof ASTPrimaryPrefix)) {
            ASTPrimaryPrefix pp = (ASTPrimaryPrefix) ape.jjtGetChild(0);
            String name2 = getNameFromPrimaryPrefix(pp);
            if (name2 != null && name2.equals(name)) {
                return true;
            }
        }
        return false;
    }

    private String getNameFromPrimaryPrefix(ASTPrimaryPrefix pp) {
        if ((pp.jjtGetNumChildren() == 1) && (pp.jjtGetChild(0) instanceof ASTName)) {
            return ((ASTName) pp.jjtGetChild(0)).getImage();
        }
        return null;
    }
}
