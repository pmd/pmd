/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTArrayAllocation;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.InvocationNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.types.JArrayType;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.OverloadSelectionResult;
import net.sourceforge.pmd.lang.java.types.TypePrettyPrint;

public class ConfusingArgumentToVarargsMethodRule extends AbstractJavaRulechainRule {

    public ConfusingArgumentToVarargsMethodRule() {
        super(ASTArgumentList.class);
    }

    @Override
    public Object visit(ASTArgumentList argList, Object data) {
        if (argList.isEmpty()) {
            return null;
        }

        // node is the last param in an arguments list
        InvocationNode call = (InvocationNode) argList.getParent();
        OverloadSelectionResult info = call.getOverloadSelectionInfo();
        if (info.isFailed()
            || info.isVarargsCall()
            || !info.getMethodType().isVarargs()) {
            return null;
        }

        List<JTypeMirror> formals = info.getMethodType().getFormalParameters();
        JTypeMirror lastFormal = formals.get(formals.size() - 1);
        JTypeMirror expectedComponent = ((JArrayType) lastFormal).getComponentType();

        // since we know this is not a varargs call the last arg has an array type
        ASTExpression varargsArg = argList.getLastChild();
        assert varargsArg != null;
        if (varargsArg.getTypeMirror().isSubtypeOf(expectedComponent)
            && !varargsArg.getTypeMirror().equals(lastFormal)) {
            // confusing

            String message;
            if (varargsArg instanceof ASTArrayAllocation && ((ASTArrayAllocation) varargsArg).getArrayInitializer() != null) {
                message = "Unclear if a varargs or non-varargs call is intended. Cast to {0} or {0}[], or pass varargs parameters separately to clarify intent.";
            } else {
                message = "Unclear if a varargs or non-varargs call is intended. Cast to {0} or {0}[] to clarify intent.";
            }
            asCtx(data).addViolationWithMessage(varargsArg, message, TypePrettyPrint.prettyPrintWithSimpleNames(expectedComponent));
        }

        return null;
    }
}
