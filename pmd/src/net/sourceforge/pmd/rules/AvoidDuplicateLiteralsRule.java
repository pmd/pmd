/*
 * User: tom
 * Date: Nov 4, 2002
 * Time: 10:02:15 AM
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.*;

import java.util.*;
import java.text.MessageFormat;

public class AvoidDuplicateLiteralsRule extends AbstractRule {

    private Map literals = new HashMap();

    public Object visit(ASTCompilationUnit node, Object data) {
        literals.clear();
        super.visit(node, data);
        int threshold = getIntProperty("threshold");
        for (Iterator i = literals.keySet().iterator(); i.hasNext();) {
            String key = (String)i.next();
            List occurrences = (List)literals.get(key);
            if (occurrences.size() >= threshold) {
                Object[] args = new Object[] {new Integer(occurrences.size()), new Integer(((SimpleNode)occurrences.get(0)).getBeginLine())};
                String msg = MessageFormat.format(getMessage(), args);
                RuleContext ctx = (RuleContext)data;
                ctx.getReport().addRuleViolation(createRuleViolation(ctx, ((SimpleNode)occurrences.get(0)).getBeginLine(), msg));
            }
        }
        return data;
    }

    public Object visit(ASTLiteral node, Object data) {
        if (!hasFourParents(node)) {
            return data;
        }

        if (!(node.jjtGetParent().jjtGetParent().jjtGetParent().jjtGetParent() instanceof ASTArgumentList)) {
            return data;
        }

        // just catching strings for now
        if (node.getImage() == null || node.getImage().indexOf('\"') == -1) {
            return data;
        }

        if (literals.containsKey(node.getImage())) {
            List occurrences = (List)literals.get(node.getImage());
            occurrences.add(node);
        } else {
            List occurrences = new ArrayList();
            occurrences.add(node);
            literals.put(node.getImage(), occurrences);
        }

        return data;
    }

    private boolean hasFourParents(Node node) {
        Node currentNode = node;
        for (int i=0; i<4; i++) {
            if (currentNode instanceof ASTCompilationUnit) {
                return false;
            }
            currentNode = currentNode.jjtGetParent();
        }
        return true;
    }
}

