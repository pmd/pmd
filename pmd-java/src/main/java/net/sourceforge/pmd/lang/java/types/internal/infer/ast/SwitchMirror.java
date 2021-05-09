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

    // todo this is undertested for invocation contexts

    private final List<ExprMirror> branches;

    SwitchMirror(JavaExprMirrors mirrors, ASTSwitchExpression myNode, boolean isStandalone) {
        super(mirrors, myNode);
        branches = myNode.getYieldExpressions().toList(it -> factory.getPolyMirror(it, isStandalone));
    }

    @Override
    public boolean branchesMatch(Predicate<? super ExprMirror> condition) {
        return CollectionUtil.all(branches, condition);
    }
}
