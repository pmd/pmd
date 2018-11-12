/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

public class ASTFormalComment extends AbstractApexNodeBase {
    private String token;

    public ASTFormalComment(String token) {
        super(ASTFormalComment.class);
        this.token = token;
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public String getToken() {
        return token;
    }
}
