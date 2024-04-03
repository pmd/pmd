package net.sourceforge.pmd.lang.java.rule.bestpractices;

import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTArrayAllocation;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTEnumConstant;
import net.sourceforge.pmd.lang.java.ast.InvocationNode;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.OverloadSelectionResult;

public class UnnecessaryVarargsArrayCreationRule extends AbstractJavaRulechainRule {

    // we visit array allocations because they are less frequent than
    // method calls
    public UnnecessaryVarargsArrayCreationRule() {
        super(ASTArrayAllocation.class);
    }

    @Override
    public Object visit(ASTArrayAllocation array, Object data) {

        JavaNode parent = array.getParent();
        if (parent instanceof ASTArgumentList && array.getIndexInParent() == parent.getNumChildren() - 1) {
            // node is the last param in an arguments list
            InvocationNode call = (InvocationNode) parent.getParent();
            OverloadSelectionResult info = call.getOverloadSelectionInfo();
            if (info.isFailed() || info.isVarargsCall()
                || !info.getMethodType().isVarargs()) {
                return null;
            }

            asCtx(data).addViolation(array);
        }

        return null;
    }
}
