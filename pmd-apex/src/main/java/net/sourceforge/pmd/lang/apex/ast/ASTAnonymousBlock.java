/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.AnonymousUnit;

/**
 * @since 7.27.0
 */
public final class ASTAnonymousBlock extends AbstractApexNode.Single<AnonymousUnit> {
    ASTAnonymousBlock(AnonymousUnit node) {
        super(node);
    }

    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
