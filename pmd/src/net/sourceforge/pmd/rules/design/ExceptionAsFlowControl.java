package net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTCatch;
import net.sourceforge.pmd.ast.ASTName;
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
        
        String throwName = node.getFirstASTNameImage();
        ASTTryStatement parent = (ASTTryStatement) node.getFirstParentOfType(ASTTryStatement.class);
        if (parent == null) {
            return data;
        }
        for (parent = (ASTTryStatement) parent.getFirstParentOfType(ASTTryStatement.class)
                ; parent != null
                ; parent = (ASTTryStatement) parent.getFirstParentOfType(ASTTryStatement.class)) {
            
            List list = parent.getCatchBlocks();
            for (Iterator iter = list.iterator(); iter.hasNext();) {
                ASTCatch catchStmt = (ASTCatch) iter.next();
                ASTType type = (ASTType) catchStmt.getFormalParameter().findChildrenOfType(ASTType.class).get(0);
                ASTName name = (ASTName) type.findChildrenOfType(ASTName.class).get(0);
                
                if (throwName != null && throwName.equals(name.getImage())) {
                    addViolation((RuleContext) data, name.getBeginLine());
                }
            }
        }
        return data;
    }

}
