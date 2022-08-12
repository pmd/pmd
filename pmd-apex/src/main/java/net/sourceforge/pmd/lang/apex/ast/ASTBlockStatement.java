/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.Node;
import net.sourceforge.pmd.annotation.InternalApi;

public class ASTBlockStatement extends AbstractApexNode.Single<Node> {
    private boolean curlyBrace;

    @Deprecated
    @InternalApi
    public ASTBlockStatement(Node blockStatement) {
        super(blockStatement);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public boolean hasCurlyBrace() {
        return curlyBrace;
    }

    @Override
    protected void handleSourceCode(final String source) {
        if (!hasRealLoc()) {
            return;
        }

        // check, whether the this block statement really begins with a curly brace
        // unfortunately, for-loop and if-statements always contain a block statement,
        // regardless whether curly braces where present or not.
        // char firstChar = source.charAt(node.getLoc().getStartIndex());
        // curlyBrace = firstChar == '{';
        // TODO(b/239648780)
    }
}
