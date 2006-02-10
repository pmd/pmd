package net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTCatchStatement;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.ast.ASTFormalParameter;
import net.sourceforge.pmd.ast.ASTThrowStatement;
import net.sourceforge.pmd.ast.ASTTryStatement;
import net.sourceforge.pmd.ast.ASTType;

import java.util.Iterator;
import java.util.List;

/**
 * Catches the use of exception statements as a flow control device.
 *
 * @author Will Sargent
 */
public class ExceptionAsFlowControl extends AbstractRule {

    public Object visit(ASTThrowStatement node, Object data) {
        ASTTryStatement parent = (ASTTryStatement) node.getFirstParentOfType(ASTTryStatement.class);
        if (parent == null) {
            return data;
        }
        for (parent = (ASTTryStatement) parent.getFirstParentOfType(ASTTryStatement.class)
                ; parent != null
                ; parent = (ASTTryStatement) parent.getFirstParentOfType(ASTTryStatement.class)) {

            List list = parent.findChildrenOfType(ASTCatchStatement.class);
            for (Iterator iter = list.iterator(); iter.hasNext();) {
                ASTCatchStatement catchStmt = (ASTCatchStatement) iter.next();
                ASTFormalParameter fp = (ASTFormalParameter) catchStmt.jjtGetChild(0);
                ASTType type = (ASTType) fp.findChildrenOfType(ASTType.class).get(0);
                ASTClassOrInterfaceType name = (ASTClassOrInterfaceType) type.findChildrenOfType(ASTClassOrInterfaceType.class).get(0);
                if (node.getFirstClassOrInterfaceTypeImage() != null && node.getFirstClassOrInterfaceTypeImage().equals(name.getImage())) {
                    addViolation(data, name);
                }
            }
        }
        return data;
    }

}
