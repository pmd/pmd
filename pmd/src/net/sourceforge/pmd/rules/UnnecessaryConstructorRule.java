/*
 * User: tom
 * Date: Nov 1, 2002
 * Time: 8:54:28 AM
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.*;

import java.util.ArrayList;
import java.util.List;

public class UnnecessaryConstructorRule extends AbstractRule {

    private List constructors;

    // TODO - modify this to get the constructors from the symbol table
    public Object visit(ASTCompilationUnit node, Object data) {
        constructors = new ArrayList();
        node.findChildrenOfType(ASTConstructorDeclaration.class, constructors);

        // there must be be the only constructor
        // TODO this gets thrown off by inner classes
        if (constructors.size() > 1) {
            return data;
        }

        return super.visit(node, data);
    }

    public Object visit(ASTConstructorDeclaration node, Object data) {
        // must be public
        AccessNode accessNode = (AccessNode)node;
        if (!accessNode.isPublic()) {
            return data;
        }

        // must have no parameters
        ASTFormalParameters params = (ASTFormalParameters)accessNode.jjtGetChild(0);
        if (params.jjtGetNumChildren() > 0) {
            return data;
        }

        // the constructor must be empty
        if (node.jjtGetNumChildren() > 1) {
            return data;
        }

        RuleContext ctx = (RuleContext)data;
        ctx.getReport().addRuleViolation(createRuleViolation(ctx, node.getBeginLine()));

        return data;
    }
}
