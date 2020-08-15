/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.types.internal.infer.ast;

import java.util.function.Predicate;

import net.sourceforge.pmd.lang.java.ast.ASTConditionalExpression;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror.BranchingMirror;

class ConditionalMirrorImpl extends BasePolyMirror<ASTConditionalExpression> implements BranchingMirror {

    ExprMirror thenBranch;
    ExprMirror elseBranch;

    ConditionalMirrorImpl(JavaExprMirrors mirrors, ASTConditionalExpression expr) {
        super(mirrors, expr);
        thenBranch = mirrors.getMirror(myNode.getThenBranch());
        elseBranch = mirrors.getMirror(myNode.getElseBranch());
    }


    @Override
    public boolean branchesMatch(Predicate<? super ExprMirror> condition) {
        return condition.test(thenBranch) && condition.test(elseBranch);
    }

}
