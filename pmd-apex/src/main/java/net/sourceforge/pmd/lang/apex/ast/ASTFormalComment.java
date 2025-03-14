/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import org.antlr.v4.runtime.Token;

import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.document.TextRegion;

public final class ASTFormalComment extends AbstractApexNode.Empty {

    private final Token token;

    ASTFormalComment(Token token) {
        this.token = token;
    }

    
    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    @Override
    public String getImage() {
        return token.getText();
    }

    @Override
    protected void calculateTextRegion(TextDocument sourceCode) {
        setRegion(TextRegion.fromBothOffsets(token.getStartIndex(), token.getStopIndex() + 1));
    }
}
