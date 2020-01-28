/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * A break statement, that jumps to a named label (or exits the current loop).
 *
 * <pre class="grammar">
 *
 * BreakStatement ::= "break" &lt;IDENTIFIER&gt;? ";"
 *
 * </pre>
 *
 */
public final class ASTBreakStatement extends AbstractStatement {

    ASTBreakStatement(int id) {
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

    public String getLabel() {
        return getImage();
    }

    @Override
    public String getImage() {
        String result = super.getImage();
        if (result == null && hasDescendantOfType(ASTName.class)) {
            result = getFirstDescendantOfType(ASTName.class).getImage();
        }
        return result;
    }

}
