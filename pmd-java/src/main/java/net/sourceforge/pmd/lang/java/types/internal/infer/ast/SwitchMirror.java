/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.internal.infer.ast;

import java.util.List;
import java.util.function.Predicate;

import net.sourceforge.pmd.lang.java.ast.ASTSwitchExpression;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror.BranchingMirror;
import net.sourceforge.pmd.util.CollectionUtil;

class SwitchMirror extends BasePolyMirror<ASTSwitchExpression> implements BranchingMirror {

    // This doesn't require an impl for getStandaloneType
    // If we explore it during overload resolution/ type inference, it's
    // because it's in an invocation ctx (or assignment, as the return of a lambda)
    // Rules are more complicated for ternary exprs, and they require this.

    private final List<ExprMirror> branches;

    SwitchMirror(JavaExprMirrors mirrors, ASTSwitchExpression myNode) {
        super(mirrors, myNode);
        branches = myNode.getYieldExpressions().toList(factory::getMirror);
    }


    @Override
    public boolean branchesMatch(Predicate<? super ExprMirror> condition) {
        return CollectionUtil.all(branches, condition);
    }
}
