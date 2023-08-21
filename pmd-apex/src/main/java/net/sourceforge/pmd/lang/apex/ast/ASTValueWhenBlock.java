/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.statement.ValueWhenBlock;

public final class ASTValueWhenBlock extends AbstractApexNode<ValueWhenBlock> {


    ASTValueWhenBlock(ValueWhenBlock node) {
        super(node);
    }



    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
