/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Iterator;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.annotation.DeprecatedUntil700;
import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;

/**
 * Represents an annotation.
 *
 * <pre class="grammar">
 *
 * Annotation ::= "@" {@link ASTClassOrInterfaceType ClassName} {@link ASTAnnotationMemberList AnnotationMemberList}?
 *
 * </pre>
 */
public final class ASTAnnotation extends AbstractJavaTypeNode implements TypeNode, ASTMemberValue, Iterable<ASTMemberValuePair> {

    ASTAnnotation(int id) {
        super(id);
    }


    /**
     * Returns the node that represents the name of the annotation.
     */
    public ASTClassOrInterfaceType getTypeNode() {
        return (ASTClassOrInterfaceType) getChild(0);
    }

    /**
     * Returns the symbol of the annotation type.
     */
    public JClassSymbol getSymbol() {
        // This cast would fail if you use a type parameter as an
        // annotation name. This is reported as an error by the
        // disambiguation pass
        return (JClassSymbol) getTypeNode().getReferencedSym();
    }

    /**
     * Returns the name of the annotation as it is used,
     * eg {@code java.lang.Override} or {@code Override}.
     *
     * @deprecated Use {@link #getSymbol()} instead.
     */
    @Deprecated
    @DeprecatedUntil700
    public String getAnnotationName() {
        return (String) getTypeNode().getText();
    }

    /**
     * Returns the simple name of the annotation.
     */
    public String getSimpleName() {
        return getTypeNode().getSimpleName();
    }


    /**
     * Returns the list of members, or null if there is none.
     */
    public @Nullable ASTAnnotationMemberList getMemberList() {
        return children().first(ASTAnnotationMemberList.class);
    }

    /**
     * Returns the stream of explicit members for this annotation.
     */
    public NodeStream<ASTMemberValuePair> getMembers() {
        return children(ASTAnnotationMemberList.class).children(ASTMemberValuePair.class);
    }


    @Override
    public Iterator<ASTMemberValuePair> iterator() {
        return children(ASTMemberValuePair.class).iterator();
    }


    @Override
    public <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

}
