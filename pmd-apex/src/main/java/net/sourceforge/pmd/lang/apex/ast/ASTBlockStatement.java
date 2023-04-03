/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.Node;
import com.google.summit.ast.SourceLocation;

public final class ASTBlockStatement extends AbstractApexNode.Single<Node> {
    private boolean curlyBrace;

    ASTBlockStatement(Node blockStatement) {
        super(blockStatement);
    }


    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    public boolean hasCurlyBrace() {
        return curlyBrace;
    }

    @Override
    void closeNode(TextDocument document) {
        super.closeNode(document);
        if (!hasRealLoc()) {
            return;
        }

        // check, whether the this block statement really begins with a curly brace
        // unfortunately, for-loop and if-statements always contain a block statement,
        // regardless whether curly braces where present or not.
        SourceLocation loc = node.getSourceLocation();
        String sourceRegion = loc.extractFrom(source);
        this.curlyBrace = sourceRegion.charAt(0) == '{';
    }

    /* TODO post-merge
    @Override
    public boolean hasRealLoc() {
        return super.hasRealLoc() && !Objects.equals(node.getLoc(), getParent().getNode().getLoc());
    }
    */
}
