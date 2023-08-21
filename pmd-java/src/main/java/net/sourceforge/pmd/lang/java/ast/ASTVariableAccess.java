/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccToken;
import net.sourceforge.pmd.lang.java.ast.ASTAssignableExpr.ASTNamedReferenceExpr;
import net.sourceforge.pmd.lang.java.types.JVariableSig;

/**
 * An unqualified reference to a variable (either local, or a field that
 * is in scope).
 *
 * <pre class="grammar">
 *
 * VariableAccess ::= &lt;IDENTIFIER&gt;
 *
 * </pre>
 *
 * @implNote {@linkplain ASTAmbiguousName Ambiguous names} are promoted
 *     to this status in the syntactic contexts, where we know they're definitely
 *     variable references.
 */
public final class ASTVariableAccess extends AbstractJavaExpr implements ASTNamedReferenceExpr {

    private JVariableSig typedSym;

    /**
     * Constructor promoting an ambiguous name to a variable reference.
     */
    ASTVariableAccess(ASTAmbiguousName name) {
        super(JavaParserImplTreeConstants.JJTVARIABLEACCESS);
        setImage(name.getFirstToken().getImage());
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

    @Override
    public String getName() {
        return getImage();
    }


    @Override
    public @Nullable JVariableSig getSignature() {
        if (typedSym == null) {
            forceTypeResolution(); // this will do it only once, even if it fails
        }
        return typedSym;
    }

    void setTypedSym(JVariableSig sig) {
        this.typedSym = sig;
        assert typedSym != null : "Null signature?";
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }
}
