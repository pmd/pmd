/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.apache.commons.lang3.StringEscapeUtils;


/**
 * Represents a string literal. The image of this node can be the literal as it appeared
 * in the source, but JavaCC performs its own unescaping and some escapes may be lost.
 * At the very least it has delimiters. {@link #getUnescapedValue()} allows to recover
 * the actual runtime value.
 */
public final class ASTStringLiteral extends AbstractLiteral implements ASTLiteral {

    private boolean isTextBlock;

    ASTStringLiteral(int id) {
        super(id);
    }


    ASTStringLiteral(JavaParser p, int id) {
        super(p, id);
    }


    private String reconstructedImage = null;


    @Override
    public String getImage() {
        if (reconstructedImage == null) {
            reconstructedImage = isTextBlock ? super.getImage() : getEscapedStringLiteral(super.getImage());
        }
        return reconstructedImage;
    }


    /**
     * Tries to reconstruct the original string literal. If the original length
     * is greater than the parsed String literal, then probably some unicode
     * escape sequences have been used.
     */
    private String getEscapedStringLiteral(String javaccEscaped) {
        int fullLength = getEndColumn() - getBeginColumn();
        if (fullLength > javaccEscaped.length()) {
            StringBuilder result = new StringBuilder(fullLength);
            for (int i = 0; i < javaccEscaped.length(); i++) {
                char c = javaccEscaped.charAt(i);
                if (c < 0x20 || c > 0xff || javaccEscaped.length() == 1) {
                    String hex = "0000" + Integer.toHexString(c);
                    result.append("\\u").append(hex.substring(hex.length() - 4));
                } else {
                    result.append(c);
                }
            }
            return result.toString();
        }
        return javaccEscaped;
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
        String image = getImage();
        String woDelims = image.substring(1, image.length() - 1);
        return StringEscapeUtils.unescapeJava(woDelims);
    }

}
