/*
 * User: tom
 * Date: Jul 22, 2002
 * Time: 11:35:50 AM
 */
package net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTResultType;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.ASTFieldDeclaration;

import java.util.Set;
import java.util.HashSet;
import java.text.MessageFormat;

public class LooseCouplingRule extends AbstractRule {

    private Set implClassNames = new HashSet();

    public LooseCouplingRule() {
        super();
        implClassNames.add("HashSet");
        implClassNames.add("HashMap");
        implClassNames.add("ArrayList");
        implClassNames.add("LinkedList");
        implClassNames.add("LinkedHashMap");
        implClassNames.add("LinkedHashSet");
        implClassNames.add("TreeSet");
        implClassNames.add("TreeMap");
    }

    public Object visit(ASTResultType node, Object data) {
        return checkType(node, data);
    }

    public Object visit(ASTFieldDeclaration node, Object data) {
        return checkType(node, data);
    }

    private Object checkType(SimpleNode node, Object data) {
        if (node.jjtGetNumChildren() ==0) {
            return data;
        }
        SimpleNode returnTypeNameNode = (SimpleNode)node.jjtGetChild(0).jjtGetChild(0);
        if (implClassNames.contains(returnTypeNameNode.getImage())) {
            RuleContext ctx = (RuleContext)data;
            ctx.getReport().addRuleViolation(createRuleViolation(ctx, returnTypeNameNode.getBeginLine(), MessageFormat.format(getMessage(), new Object[] {returnTypeNameNode.getImage()})));
        }
        return data;
    }
}
