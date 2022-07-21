/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;


import com.google.summit.ast.Node;
import org.antlr.runtime.Token;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTFormalComment extends AbstractApexNode<Node> {

    private final String image;

    ASTFormalComment(Token token) {
        // super(new AstComment(token));
        // TODO(b/239648780)
        super(null);
        this.image = token.getText();
    }

    @Deprecated
    public ASTFormalComment(String token) {
        // super(new AstComment(null));
        // TODO(b/239648780)
        super(null);
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

    /*
    @Deprecated
    @InternalApi
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
     */
    // TODO(b/239648780)
}
