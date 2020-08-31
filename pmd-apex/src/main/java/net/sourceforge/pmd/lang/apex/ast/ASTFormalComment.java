/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;


import net.sourceforge.pmd.lang.apex.ast.ASTFormalComment.AstComment;
import net.sourceforge.pmd.util.document.Chars;
import net.sourceforge.pmd.util.document.TextRegion;

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

public final class ASTFormalComment extends AbstractApexNode<AstComment> {

    private final Chars image;

    ASTFormalComment(TextRegion token, Chars image) {
        super(new AstComment(token));
        this.image = image;
    }


    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    @Override
    public String getImage() {
        return image.toString();
    }

    public Chars getToken() {
        return image;
    }


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

}
