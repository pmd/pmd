/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.Node;

/**
 * @deprecated Since 7.19.0. This AST node is not used and doesn't appear in the tree.
 */
@Deprecated
public final class ASTStatement extends AbstractApexNode.Single<Node> {

    ASTStatement(Node statement) {
        super(statement);
    }


    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
