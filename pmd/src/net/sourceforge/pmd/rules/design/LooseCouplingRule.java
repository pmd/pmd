/*
 * User: tom
 * Date: Jul 22, 2002
 * Time: 11:35:50 AM
 */
package net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.ast.ASTFormalParameter;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTResultType;
import net.sourceforge.pmd.ast.Node;

import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

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
        implClassNames.add("Vector");
        implClassNames.add("java.util.HashSet");
        implClassNames.add("java.util.HashMap");
        implClassNames.add("java.util.ArrayList");
        implClassNames.add("java.util.LinkedList");
        implClassNames.add("java.util.LinkedHashMap");
        implClassNames.add("java.util.LinkedHashSet");
        implClassNames.add("java.util.TreeSet");
        implClassNames.add("java.util.TreeMap");
        implClassNames.add("java.util.Vector");
    }

    public Object visit(ASTName node, Object data) {
        Node parent = node.jjtGetParent().jjtGetParent();
        if (implClassNames.contains(node.getImage()) && (parent instanceof ASTFieldDeclaration || parent instanceof ASTFormalParameter || parent instanceof ASTResultType)) {
            RuleContext ctx = (RuleContext)data;
            ctx.getReport().addRuleViolation(createRuleViolation(ctx, node.getBeginLine(), MessageFormat.format(getMessage(), new Object[] {node.getImage()})));
        }
        return data;
    }
}
