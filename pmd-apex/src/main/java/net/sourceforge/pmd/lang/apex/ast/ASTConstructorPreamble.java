/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.compilation.ConstructorPreamble;

public final class ASTConstructorPreamble extends AbstractApexNode<ConstructorPreamble> {

    ASTConstructorPreamble(ConstructorPreamble node) {
        super(node);
    }


    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
