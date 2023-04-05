/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import org.antlr.v4.runtime.Token;

import net.sourceforge.pmd.lang.document.TextFileContent;
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

    /* TODO: needed?
    public Chars getToken() {
        String text = token.getText();
        return Chars.fromString(text, 0, text.length());
    }
    */

    @Override
    protected void calculateTextRegion(TextFileContent sourceContent) {
	setRegion(TextRegion.fromBothOffsets(token.getStartIndex(), token.getStopIndex()));
    }
}
