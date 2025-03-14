/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.lang.ast.AstVisitorBase;

public abstract class ApexVisitorBase<P, R> extends AstVisitorBase<P, R> implements ApexVisitor<P, R> {

    @Override
    public R visit(ASTUserInterface node, P data) {
        return visitTypeDecl(node, data);
    }

    @Override
    public R visit(ASTUserClass node, P data) {
        return visitTypeDecl(node, data);
    }

    public R visitTypeDecl(ASTUserClassOrInterface<?> node, P data) {
        return visitApexNode(node, data);
    }

}
