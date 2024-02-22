/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.lang.document.TextDocument;

import com.google.summit.ast.Node;

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
    protected void calculateTextRegion(TextDocument sourceCode) {
        super.calculateTextRegion(sourceCode);
        if (!hasRealLoc()) {
            return;
        }

        // check, whether this block statement really begins with a curly brace
        // unfortunately, for-loop and if-statements always contain a block statement,
        // regardless whether curly braces where present or not.
        this.curlyBrace = sourceCode.getText().slice(getTextRegion()).charAt(0) == '{';
    }
}
