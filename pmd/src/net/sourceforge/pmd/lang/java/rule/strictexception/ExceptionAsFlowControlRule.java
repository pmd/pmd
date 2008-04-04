package net.sourceforge.pmd.lang.java.rule.strictexception;

import net.sourceforge.pmd.lang.java.ast.ASTCatchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.java.ast.ASTThrowStatement;
import net.sourceforge.pmd.lang.java.ast.ASTTryStatement;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

import java.util.List;

/**
 * Catches the use of exception statements as a flow control device.
 *
 * @author Will Sargent
 */
public class ExceptionAsFlowControlRule extends AbstractJavaRule {

    public Object visit(ASTThrowStatement node, Object data) {
        ASTTryStatement parent = node.getFirstParentOfType(ASTTryStatement.class);
        if (parent == null) {
            return data;
        }
        for (parent = parent.getFirstParentOfType(ASTTryStatement.class)
                ; parent != null
                ; parent = parent.getFirstParentOfType(ASTTryStatement.class)) {

            List<ASTCatchStatement> list = parent.findChildrenOfType(ASTCatchStatement.class);
            for (ASTCatchStatement catchStmt: list) {
                ASTFormalParameter fp = (ASTFormalParameter) catchStmt.jjtGetChild(0);
                ASTType type = fp.findChildrenOfType(ASTType.class).get(0);
                ASTClassOrInterfaceType name = type.findChildrenOfType(ASTClassOrInterfaceType.class).get(0);
                if (node.getFirstClassOrInterfaceTypeImage() != null && node.getFirstClassOrInterfaceTypeImage().equals(name.getImage())) {
                    addViolation(data, name);
                }
            }
        }
        return data;
    }

}
