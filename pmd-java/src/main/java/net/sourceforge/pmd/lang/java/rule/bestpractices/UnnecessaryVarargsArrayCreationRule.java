/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTArrayAllocation;
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
        if (array.getArrayInitializer() == null) {
            return null;
        }

        JavaNode parent = array.getParent();
        if (parent instanceof ASTArgumentList && array.getIndexInParent() == parent.getNumChildren() - 1) {
            // node is the last param in an arguments list
            InvocationNode call = (InvocationNode) parent.getParent();
            OverloadSelectionResult info = call.getOverloadSelectionInfo();
            if (info.isFailed() || info.isVarargsCall()
                || !info.getMethodType().isVarargs()) {
                return null;
            }

            List<JTypeMirror> formals = info.getMethodType().getFormalParameters();
            JTypeMirror lastFormal = formals.get(formals.size() - 1);

            if (array.getTypeMirror().equals(lastFormal)) {
                // If type not equal, then it would not actually be equivalent to remove the array creation.
                // That case may be caught by ConfusingArgumentToVarargsMethod
                asCtx(data).addViolation(array);
            }
        }

        return null;
    }
}
