/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules.junit;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTBlock;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.ast.ASTResultType;
import net.sourceforge.pmd.ast.ASTStatementExpression;
import net.sourceforge.pmd.ast.Node;

public class JUnitTestsShouldContainAsserts extends AbstractRule implements Rule {

    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (node.isInterface()) {
            return data;
        }
        return super.visit(node, data);
    }

/*
FIXME
	public Object visit(ASTNestedInterfaceDeclaration node, Object data) {
		// skip also internal interfaces, bug [ 1146116 ] JUnitTestsShouldIncludeAssert crashes on inner Interface
		return data;
	}
*/

    public Object visit(ASTMethodDeclaration declaration, Object data) {
        if (!declaration.isPublic() || declaration.isAbstract() || declaration.isNative()) {
            return data; // skip various inapplicable method variations
        }

        if (declaration.jjtGetNumChildren()==3) {
            ASTResultType resultType = (ASTResultType) declaration.jjtGetChild(0);
            ASTMethodDeclarator declarator = (ASTMethodDeclarator) declaration.jjtGetChild(1);
            ASTBlock block = (ASTBlock) declaration.jjtGetChild(2);
            if (resultType.isVoid() && declarator.getImage().startsWith("test")) {
                if (!hasAssertStatement(block)) {
                    RuleContext ctx = (RuleContext)data;
                    ctx.getReport().addRuleViolation(createRuleViolation(ctx, declaration));
                }
            }
        }
		return data;
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
