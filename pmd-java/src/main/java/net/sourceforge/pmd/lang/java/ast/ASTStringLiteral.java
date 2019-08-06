/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.apache.commons.lang3.StringEscapeUtils;

/**
 * Represents a string literal. The image of this node is the literal as it appeared
 * in the source ({@link #getText()}). {@link #getUnescapedValue()} allows to recover
 * the actual runtime value, by processing escapes.
 */
public final class ASTStringLiteral extends AbstractLiteral implements ASTLiteral {

    private boolean isTextBlock;

    ASTStringLiteral(int id) {
        super(id);
    }


    ASTStringLiteral(JavaParser p, int id) {
        super(p, id);
    }


    @Override
    public String getImage() {
        return getText().toString();
    }

    void setTextBlock() {
        this.isTextBlock = true;
    }

    /** Returns true if this is a text block (currently Java 13 preview feature). */
    public boolean isTextBlock() {
        return isTextBlock;
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
     * Returns the value without delimiters and unescaped.
     */
    public String getUnescapedValue() {
        String image = super.getImage();
        String woDelims = image.substring(1, image.length() - 1);
        return StringEscapeUtils.unescapeJava(woDelims);
    }

}
