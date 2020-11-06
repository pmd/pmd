/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.plsql.ast;

public final class ASTStringLiteral extends net.sourceforge.pmd.lang.plsql.ast.AbstractPLSQLNode {


    ASTStringLiteral(int id) {
        super(id);
    }


    ASTStringLiteral(PLSQLParser p, int id) {
        super(p, id);
    }


    @Override
    public Object jjtAccept(PLSQLParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    /**
     * Gets the plain string from the string literal.
     * @return the plain string value from the string literal.
     */
    public String getString() {
        String image = getImage();
        if (image.charAt(0) == 'N' || image.charAt(0) == 'n') {
            image = image.substring(1);
        }

        if (image.charAt(0) == '\'') {
            image = image.substring(1, image.length() - 1);
        } else if (image.charAt(0) == 'Q' || image.charAt(0) == 'q') {
            image = image.substring("q'x".length(), image.length() - 2);
        }
        return image;
    }
}
