/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import org.antlr.v4.runtime.Token;

import net.sourceforge.pmd.lang.ast.SourceCodePositioner;

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
    void calculateLineNumbers(SourceCodePositioner positioner) {
        this.beginLine = positioner.lineNumberFromOffset(token.getStartIndex());
        this.beginColumn = positioner.columnFromOffset(this.beginLine, token.getStartIndex());
        this.endLine = positioner.lineNumberFromOffset(token.getStopIndex());
        this.endColumn = positioner.columnFromOffset(this.endLine, token.getStopIndex());
    }


    /* TODO post-merge
    static final class AstComment implements AstNode {

        private final Location loc;

        private AstComment(TextRegion region) {
            this.loc = Locations.index(region.getStartOffset(), region.getLength());
        }

        @Override
        public Location getLoc() {
            return loc;
        }

        @Override
        public <T extends Scope> void traverse(AstVisitor<T> astVisitor, T t) {
            // do nothing
        }

        @Override
        public void validate(SymbolResolver symbolResolver, ValidationScope validationScope) {
            // do nothing
        }

        @Override
        public void emit(Emitter emitter) {
            // do nothing
        }

        @Override
        public TypeInfo getDefiningType() {
            return TypeInfos.VOID;
        }
    }
    */

    @Override
    public String getLocation() {
        return String.format("[%d:%d,%d:%d]", this.beginLine, this.beginColumn,
                this.endLine, this.endColumn);
    }
}
