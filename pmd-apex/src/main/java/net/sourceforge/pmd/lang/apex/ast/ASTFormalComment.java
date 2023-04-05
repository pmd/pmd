/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import org.antlr.v4.runtime.Token;

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

    public String getToken() {
        return token.getText();
    }

    @Override
    void calculateTextRegion(TextFileContent sourceContent) {
	this.region = TextRegion(token.getStartIndex(), token.getStopIndex());
    }

    @Override
    public String getLocation() {
        return String.format("[%d:%d,%d:%d]", this.beginLine, this.beginColumn,
                this.endLine, this.endColumn);
    }
}
