/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;


import org.antlr.runtime.Token;

import net.sourceforge.pmd.lang.apex.ast.ASTFormalComment.AstComment;

import apex.jorje.data.Location;
import apex.jorje.data.Locations;
import apex.jorje.semantic.ast.AstNode;
import apex.jorje.semantic.ast.context.Emitter;
import apex.jorje.semantic.ast.visitor.AstVisitor;
import apex.jorje.semantic.ast.visitor.Scope;
import apex.jorje.semantic.ast.visitor.ValidationScope;
import apex.jorje.semantic.symbol.resolver.SymbolResolver;
import apex.jorje.semantic.symbol.type.TypeInfo;
import apex.jorje.semantic.symbol.type.TypeInfos;

public class ASTFormalComment extends AbstractApexNode<AstComment> {

    private final String image;

    ASTFormalComment(Token token) {
        super(new AstComment(token));
        this.image = token.getText();
    }

    @Deprecated
    public ASTFormalComment(String token) {
        super(new AstComment(null));
        image = token;
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public String getImage() {
        return image;
    }

    public String getToken() {
        return image;
    }


    public static final class AstComment implements AstNode {

        private final Location loc;

        private AstComment(Token token) {
            this.loc = token == null
                       ? Locations.NONE
                       : Locations.loc(token.getLine(), token.getCharPositionInLine() + 1);
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

}
