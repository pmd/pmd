/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.internal.infer.ast;

import java.util.stream.Stream;

import net.sourceforge.pmd.lang.java.ast.ASTSwitchExpression;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror;
import net.sourceforge.pmd.lang.java.types.internal.infer.ExprMirror.BranchingMirror;

class SwitchMirror extends BasePolyMirror<ASTSwitchExpression> implements BranchingMirror {

    // todo standalone types

    SwitchMirror(JavaExprMirrors mirrors, ASTSwitchExpression myNode) {
        super(mirrors, myNode);
    }


    @Override
    public Stream<ExprMirror> getBranches() {
        return myNode.getYieldExpressions().toStream().map(factory::getMirror);
    }
}
