/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import org.antlr.v4.runtime.Token;

import net.sourceforge.pmd.lang.ast.SourceCodePositioner;

public class ASTFormalComment extends AbstractApexNode.Empty {

    private final Token token;

    ASTFormalComment(Token token) {
        this.token = token;
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
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
    void calculateLineNumbers(SourceCodePositioner positioner) {
        this.beginLine = positioner.lineNumberFromOffset(token.getStartIndex());
        this.beginColumn = positioner.columnFromOffset(this.beginLine, token.getStartIndex());
        this.endLine = positioner.lineNumberFromOffset(token.getStopIndex());
        this.endColumn = positioner.columnFromOffset(this.endLine, token.getStopIndex());
    }

    @Override
    public boolean hasRealLoc() {
        return true;
    }

    @Override
    public String getLocation() {
        return String.format("[%d:%d,%d:%d]", this.beginLine, this.beginColumn,
                this.endLine, this.endColumn);
    }
}
