/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.lang.ast.AstVisitorBase;

public class ApexVisitorBase<P, R> extends AstVisitorBase<P, R> implements ApexVisitor<P, R> {


    @Override
    public final R visit(ASTUserInterface node, P data) {
        return visitClassOrInterface(node, data);
    }

    @Override
    public final R visit(ASTUserClass node, P data) {
        return visitClassOrInterface(node, data);
    }

    public R visitClassOrInterface(ASTUserClassOrInterface<?> node, P data) {
        return visitApexNode(node, data);
    }

}
