/*
 * User: tom
 * Date: Oct 10, 2002
 * Time: 9:37:40 AM
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.symboltable.NameOccurrence;
import net.sourceforge.pmd.ast.*;

import java.util.Iterator;
import java.util.Map;
import java.util.List;

public class StringToStringRule extends AbstractRule {

    public Object visit(ASTVariableDeclaratorId node, Object data) {
        SimpleNode nameNode = node.getTypeNameNode();
        if (nameNode instanceof ASTPrimitiveType || !nameNode.getImage().equals("String")) {
            return data;
        }
        // now we know we're at a node of type String
        Map decls = node.getScope().getUsedVariableDeclarations();
        for (Iterator i = decls.keySet().iterator(); i.hasNext();) {
            VariableNameDeclaration decl = (VariableNameDeclaration)i.next();
            if (!decl.getImage().equals(node.getImage())) {
                continue;
            }
            List usages = (List)decls.get(decl);
            for (Iterator j = usages.iterator(); j.hasNext();) {
                NameOccurrence occ = (NameOccurrence)j.next();
                if (occ.getNameForWhichThisIsAQualifier() != null && occ.getNameForWhichThisIsAQualifier().getImage().indexOf("toString") != -1) {
                    RuleContext ctx = (RuleContext)data;
                    ctx.getReport().addRuleViolation(createRuleViolation(ctx, occ.getBeginLine()));
                }
            }
        }
        return data;
    }
}
