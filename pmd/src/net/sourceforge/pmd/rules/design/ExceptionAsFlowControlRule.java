package net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTCatch;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTThrowStatement;
import net.sourceforge.pmd.ast.ASTTryStatement;
import net.sourceforge.pmd.ast.ASTType;
import net.sourceforge.pmd.ast.Node;

import java.util.Iterator;
import java.util.List;

/**
 * Catches the use of exception statements as a flow control device.
 *
 * @author Will Sargent
 */
public class ExceptionAsFlowControlRule extends AbstractRule {
    public Object visit(ASTThrowStatement node, Object data) {
        String throwName = getThrowsName(node);
        for (Node parent = node.jjtGetParent(); parent != null; parent = parent.jjtGetParent()) {
            if (parent instanceof ASTTryStatement) {
                List list = ((ASTTryStatement) parent).getCatchBlocks();
                for (Iterator iter = list.iterator(); iter.hasNext();) {
                    ASTCatch catchStmt = (ASTCatch) iter.next();
                    ASTType type = (ASTType) catchStmt.getFormalParameter().findChildrenOfType(ASTType.class).get(0);
                    ASTName name = (ASTName) type.findChildrenOfType(ASTName.class).get(0);
                    if (throwName != null && throwName.equals(name.getImage())) {
                        ((RuleContext) data).getReport().addRuleViolation(createRuleViolation((RuleContext) data, name.getBeginLine()));
                    }
                }
            }
        }
        return data;
    }

    private String getThrowsName(ASTThrowStatement node) {
		Node childNode = node;
		while (childNode.jjtGetNumChildren() > 0) {
			childNode = childNode.jjtGetChild(0);
		}
		if (childNode instanceof ASTName) {
            return ((ASTName) childNode).getImage();
		}
		return null;
    }
}
