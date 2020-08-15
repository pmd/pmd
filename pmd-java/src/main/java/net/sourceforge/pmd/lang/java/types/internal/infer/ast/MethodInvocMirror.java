/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.internal.infer.ast;

import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTTypeExpression;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.TypeConversion;
import net.sourceforge.pmd.lang.java.types.TypeOps;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror.InvocationMirror;

class MethodInvocMirror extends BaseInvocMirror<ASTMethodCall> implements InvocationMirror {
    /*
     * method calls with explicit type arguments are standalone. To reduce the
     * number of branches in the code they still go through Infer, so that their
     * method type is set like all the others. So don't override getStandaloneType.
     */


    MethodInvocMirror(JavaExprMirrors mirrors, ASTMethodCall call) {
        super(mirrors, call);
    }

    @Override
    public List<JMethodSig> getAccessibleCandidates() {
        TypeNode lhs = myNode.getQualifier();
        if (lhs == null) {
            // already filters accessibility
            return myNode.getSymbolTable().methods().resolve(getName());
        } else {
            JTypeMirror lhsType = TypeConversion.capture(lhs.getTypeMirror());
            boolean staticOnly = lhs instanceof ASTTypeExpression;

            return TypeOps.getMethodsOf(lhsType, getName(), staticOnly, myNode.getEnclosingType().getSymbol());
        }
    }


    @Override
    public JTypeMirror getErasedReceiverType() {
        return getReceiverType().getErasure();
    }

    @Override
    public @NonNull JTypeMirror getReceiverType() {
        ASTExpression qualifier = myNode.getQualifier();
        if (qualifier != null) {
            return qualifier.getTypeMirror();
        } else {
            return myNode.getEnclosingType().getTypeMirror();
        }
    }

    @Override
    public String getName() {
        return myNode.getMethodName();
    }


}
