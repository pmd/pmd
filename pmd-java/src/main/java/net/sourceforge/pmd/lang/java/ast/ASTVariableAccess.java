/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

/**
 * A reference to an unqualified variable. {@linkplain ASTAmbiguousName Ambiguous names} are promoted
 * to this status in the syntactic contexts, where we know they're definitely variable references.
 * This node represents both references to fields and to variables (for now?).
 *
 * <pre class="grammar">
 *
 * VariableAccess ::= &lt;IDENTIFIER&gt;
 *
 * </pre>
 */
public final class ASTVariableAccess extends AbstractJavaExpr implements ASTAssignableExpr {

    /**
     * Constructor promoting an ambiguous name to a variable reference.
     */
    ASTVariableAccess(ASTAmbiguousName name) {
        super(JavaParserTreeConstants.JJTVARIABLEACCESS);
        setImage(name.getImage());
    }


    ASTVariableAccess(int id) {
        super(id);
    }


    ASTVariableAccess(JavaParser p, int id) {
        super(p, id);
    }


    /**
     * Gets the name of the referenced variable.
     */
    public String getVariableName() {
        return getImage();
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    @Override
    public <T> void jjtAccept(SideEffectingVisitor<T> visitor, T data) {
        visitor.visit(this, data);
    }
}
