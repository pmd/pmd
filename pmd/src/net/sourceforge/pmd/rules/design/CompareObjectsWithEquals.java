package net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTEqualityExpression;
import net.sourceforge.pmd.ast.ASTInitializer;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.Node;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.symboltable.MethodScope;
import net.sourceforge.pmd.symboltable.NameOccurrence;
import net.sourceforge.pmd.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.symboltable.LocalScope;
import net.sourceforge.pmd.symboltable.Scope;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class CompareObjectsWithEquals extends AbstractRule {

    private boolean hasName(Node n) {
        return n.jjtGetNumChildren() > 0 && n.jjtGetChild(0) instanceof ASTName;
    }

    public Object visit(ASTEqualityExpression node, Object data) {
        // skip if either child is not a simple name
        if (!hasName(((SimpleNode)node.jjtGetChild(0)).jjtGetChild(0)) || !hasName(((SimpleNode)node.jjtGetChild(1)).jjtGetChild(0))) {
            return data;
        }

        // skip if either is a qualified name
        if (((SimpleNode)node.jjtGetChild(0).jjtGetChild(0).jjtGetChild(0)).getImage().indexOf(".") != -1
        || ((SimpleNode)node.jjtGetChild(1).jjtGetChild(0).jjtGetChild(0)).getImage().indexOf(".") != -1) {
            return data;
        }

        // skip static initializers... missing some cases here
        if (!node.getParentsOfType(ASTInitializer.class).isEmpty()) {
            return data;
        }

        check((Scope)node.getScope(), node, data);
        check(node.getScope().getEnclosingMethodScope(), node, data);
        return data;
    }

    private void check(Scope scope, SimpleNode node, Object ctx) {
        Map vars = scope.getVariableDeclarations();
        for (Iterator i = vars.keySet().iterator(); i.hasNext();) {
            VariableNameDeclaration key = (VariableNameDeclaration)i.next();
            if (key.isPrimitiveType() || key.isArray()) {
                continue;
            }
            List usages = (List)vars.get(key);
            if (usages.isEmpty()) {
                continue;
            }
            for (Iterator j = usages.iterator(); j.hasNext();) {
                if (((NameOccurrence)j.next()).getLocation().jjtGetParent().jjtGetParent().jjtGetParent() == node) {
                    addViolation(ctx, node);
                    return;
                }
            }
        }
    }
}
