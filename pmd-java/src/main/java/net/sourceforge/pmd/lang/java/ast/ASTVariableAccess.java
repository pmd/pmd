/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;

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
        super(JavaParserImplTreeConstants.JJTVARIABLEACCESS);
        setImage(name.getImage());
    }

    ASTVariableAccess(JavaccToken identifier) {
        super(JavaParserImplTreeConstants.JJTVARIABLEACCESS);

        TokenUtils.expectKind(identifier, JavaTokenKinds.IDENTIFIER);

        setImage(identifier.getImage());
        setFirstToken(identifier);
        setLastToken(identifier);
    }


    ASTVariableAccess(int id) {
        super(id);
    }


    /**
     * Gets the name of the referenced variable.
     */
    public String getVariableName() {
        return getImage();
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
