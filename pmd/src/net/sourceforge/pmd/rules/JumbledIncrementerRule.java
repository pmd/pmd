/*
 * User: tom
 * Date: Sep 27, 2002
 * Time: 4:20:22 PM
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.*;

import java.util.*;

public class JumbledIncrementerRule extends AbstractRule {

    public Object visit(ASTForStatement node, Object data) {

        Set forUpdateNames = findForUpdateNames(node);
        if (forUpdateNames.isEmpty()) {
            return super.visit(node,data);
        }

        Node forStmtBlk = findForStmtBlock(node);
        if (forStmtBlk == null) {
            return super.visit(node, data);
        }

        Set enclosedForUpdateNames = findEnclosedForUpdateNames(forStmtBlk);
        if (enclosedForUpdateNames.isEmpty()) {
            return super.visit(node, data);
        }

        forUpdateNames.retainAll(enclosedForUpdateNames);

        if (!forUpdateNames.isEmpty()) {
            RuleContext ctx = (RuleContext)data;
            ctx.getReport().addRuleViolation(createRuleViolation(ctx, node.getBeginLine()));
        }
        return super.visit(node,data);
    }

    private Set findEnclosedForUpdateNames(Node node) {
        Set forUpdates = new HashSet();
        findForUpdateNodes(node, forUpdates);
        if (forUpdates.isEmpty()) {
            return forUpdates;
        }

        Set names = new HashSet();
        for (Iterator i = forUpdates.iterator();i.hasNext();) {
            Node forUpdate = (Node)i.next();
            findNames(forUpdate, names);
        }

        return names;
    }

    private void findForUpdateNodes(Node node, Set nodes) {
        if (node instanceof ASTForUpdate) {
            nodes.add(node);
        } else {
            for (int i=0;i<node.jjtGetNumChildren();i++) {
                Node child = node.jjtGetChild(i);
                if (child.jjtGetNumChildren() >0) {
                    findForUpdateNodes(child, nodes);
                } else {
                    if (child instanceof ASTForUpdate) {
                        nodes.add(((SimpleNode)node).getImage());
                    }
                }
            }
        }
    }

    public Node findForStmtBlock(Node node) {
        for (int i=0; i<node.jjtGetNumChildren(); i++) {
            Node child = node.jjtGetChild(i);
            if (child instanceof ASTStatement || child instanceof ASTBlock) {
                return child;
            }
        }
        return null;
    }

    private Set findForUpdateNames(Node node) {
        Set forUpdateNames = new HashSet();
        for (int i=0; i<node.jjtGetNumChildren(); i++) {
            if (node.jjtGetChild(i) instanceof ASTForUpdate) {
                findNames(node.jjtGetChild(i), forUpdateNames);
            }
        }
        return forUpdateNames;
    }

    private void findNames(Node node, Set names) {
        if (node instanceof ASTName) {
            names.add(((SimpleNode)node).getImage());
        } else {
            for (int i=0;i<node.jjtGetNumChildren();i++) {
                Node child = node.jjtGetChild(i);
                if (child.jjtGetNumChildren() >0) {
                    findNames(child, names);
                } else {
                    if (child instanceof ASTName) {
                        names.add(((SimpleNode)child).getImage());
                    }
                }
            }
        }
    }

}
