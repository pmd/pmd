/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules.junit;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTBlock;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.ast.ASTResultType;
import net.sourceforge.pmd.ast.ASTStatementExpression;
import net.sourceforge.pmd.ast.Node;

public class JUnitTestsShouldContainAsserts extends AbstractRule implements Rule {

    public Object visit(ASTMethodDeclaration declaration, Object data) {
        if (declaration.jjtGetNumChildren()==3) { // TODO can it not be 3???
            ASTResultType resultType = (ASTResultType) declaration.jjtGetChild(0);
            ASTMethodDeclarator declarator = (ASTMethodDeclarator) declaration.jjtGetChild(1);
            ASTBlock block = (ASTBlock) declaration.jjtGetChild(2);
            if (isTestCase(resultType, declarator)) {
                if (!hasAssertStatement(block)) {
                    RuleContext ctx = (RuleContext)data;
                    ctx.getReport().addRuleViolation(createRuleViolation(ctx, declaration.getBeginLine()));
                }
            }
        }
		return data;
	}
	
	/**
	 * Evalue node as a valid test method for junit.
	 * The node is evaluated the following conditions: <br>
	 * 1. image starts with test
	 * 2. return type is void
	 * TODO 3. scope is public 
	 * @param resultType result type node of method
	 *  
     * @param node ASTMethodDeclarator node
     * @return true if all three conditions match false, in any other case
     */
    private boolean isTestCase(ASTResultType resultType, ASTMethodDeclarator node) {
        if (node.getImage()==null || !node.getImage().startsWith("test"))
            return false;
        // if return type is not void
        if (resultType.jjtGetNumChildren()!=0)
            return false;
        return true;
    }

    private boolean hasAssertStatement(ASTBlock block) {
        return containsAssert(block, false);
	}
	
    private boolean containsAssert(Node n, boolean assertFound) {
        if (!assertFound) {
            if (n instanceof ASTStatementExpression) {
                if (isAssertOrFailStatement((ASTStatementExpression)n)) {
                    return true;
                }
            }
            if (!assertFound) {
                for (int i=0;i<n.jjtGetNumChildren() && ! assertFound;i++) {
                    Node c = n.jjtGetChild(i);
                    if (containsAssert(c, assertFound)) 
                        return true;
                }
            }
        }
        return false;
    }

    /**
     * Tells if the expression is an assert statement or not.
     */
    private boolean isAssertOrFailStatement(ASTStatementExpression expression) {
        if (expression!=null 
                && expression.jjtGetNumChildren()>0
                && expression.jjtGetChild(0) instanceof ASTPrimaryExpression
                ) {
            ASTPrimaryExpression pe = (ASTPrimaryExpression) expression.jjtGetChild(0);
            if (pe.jjtGetNumChildren()> 0
                    && pe.jjtGetChild(0) instanceof ASTPrimaryPrefix) {
                ASTPrimaryPrefix pp = (ASTPrimaryPrefix) pe.jjtGetChild(0);
                if (pp.jjtGetNumChildren()>0 && pp.jjtGetChild(0) instanceof ASTName) {
                    ASTName n = (ASTName) pp.jjtGetChild(0);
                    if (n.getImage()!=null && (n.getImage().startsWith("assert") || n.getImage().startsWith("fail") )) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
