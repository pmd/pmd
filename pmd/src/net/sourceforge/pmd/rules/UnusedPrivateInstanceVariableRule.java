/*
 * User: tom
 * Date: Jun 21, 2002
 * Time: 11:26:34 AM
 */
package net.sourceforge.pmd.rules;

import java.util.Iterator;
import java.text.MessageFormat;

import net.sourceforge.pmd.ast.*;
import net.sourceforge.pmd.*;
import net.sourceforge.pmd.symboltable.*;

public class UnusedPrivateInstanceVariableRule extends AbstractRule {

    /**
     * TODO - simplify this by
     * 1) visiting UnmodifiedClassDeclarations
     * 2) getting the scope
     * 3) getting all unused decls
     * 4) getting the decl node
     * 5) if it's private and not serialVersionUID, it's a rule violation
     */
    public Object visit(ASTVariableDeclaratorId node, Object data) {
        if (node.jjtGetParent().jjtGetParent() instanceof ASTFieldDeclaration) {
            ASTFieldDeclaration field = (ASTFieldDeclaration)node.jjtGetParent().jjtGetParent();
            if (field.isPrivate()) {
                RuleContext ctx = (RuleContext)data;
                for (Iterator i =  node.getScope().getUnusedDeclarations(); i.hasNext();) {
                    NameDeclaration decl = (NameDeclaration)i.next();
                    if (decl.getImage().equals("serialVersionUID") || !decl.getNode().equals(node)) {
                        continue;
                    }
                    ctx.getReport().addRuleViolation(createRuleViolation(ctx, decl.getLine(), MessageFormat.format(getMessage(), new Object[] {decl.getImage()})));
                }
            }
        }
        return data;
    }
}
