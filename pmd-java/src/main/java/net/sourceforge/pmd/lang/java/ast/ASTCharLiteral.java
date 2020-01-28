/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.apache.commons.lang3.StringEscapeUtils;


/**
 * Represents a character literal. The image of this node can be the literal as it appeared
 * in the source, but JavaCC performs its own unescaping and some escapes may be lost. At the
 * very least it has delimiters. {@link #getConstValue()} allows to recover the actual runtime value.
 */
public final class ASTCharLiteral extends AbstractLiteral implements ASTLiteral {


    ASTCharLiteral(int id) {
        super(id);
    }


    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    @Override
    public <T> void jjtAccept(SideEffectingVisitor<T> visitor, T data) {
        visitor.visit(this, data);
    }


    /**
     * Gets the char value of this literal.
     */
    @Override
    public Character getConstValue() {
        String image = getImage();
        String woDelims = image.substring(1, image.length() - 1);
        return StringEscapeUtils.unescapeJava(woDelims).charAt(0);
    }

}
