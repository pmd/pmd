/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Iterator;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.types.JClassType;

/**
 * Represents an annotation.
 *
 * <pre class="grammar">
 *
 * Annotation ::= "@" {@link ASTClassType ClassName} {@link ASTAnnotationMemberList AnnotationMemberList}?
 *
 * </pre>
 */
public final class ASTAnnotation extends AbstractJavaTypeNode implements ASTMemberValue, Iterable<ASTMemberValuePair> {

    ASTAnnotation(int id) {
        super(id);
    }


    /**
     * Returns the node that represents the name of the annotation.
     */
    public ASTClassType getTypeNode() {
        return (ASTClassType) getChild(0);
    }

    @Override
    public @NonNull JClassType getTypeMirror() {
        return (JClassType) super.getTypeMirror();
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

    /**
     * Return the expression values for the attribute with the given name.
     * This may flatten an array initializer. For example, for the attribute
     * named "value":
     * <pre>{@code
     * - @SuppressWarnings -> returns empty node stream
     * - @SuppressWarning("fallthrough") -> returns ["fallthrough"]
     * - @SuppressWarning(value={"fallthrough"}) -> returns ["fallthrough"]
     * - @SuppressWarning({"fallthrough", "rawtypes"}) -> returns ["fallthrough", "rawtypes"]
     * }</pre>
     */
    public NodeStream<ASTMemberValue> getFlatValue(String attrName) {
        return NodeStream.of(getAttribute(attrName))
                         .flatMap(ASTAnnotation::flatValue);
    }

    /**
     * Return expression values for all attributes.
     * This may flatten an array initializer. For example, for the attribute
     * named "value":
     * <pre>{@code
     * - @SuppressWarnings -> returns empty node stream
     * - @SuppressWarning("fallthrough") -> returns ["fallthrough"]
     * - @SuppressWarning(value={"fallthrough"}) -> returns ["fallthrough"]
     * - @SuppressWarning({"fallthrough", "rawtypes"}) -> returns ["fallthrough", "rawtypes"]
     * }</pre>
     */
    public NodeStream<ASTMemberValue> getFlatValues() {
        return getMembers().map(ASTMemberValuePair::getValue)
                           .flatMap(ASTAnnotation::flatValue);
    }

    private static NodeStream<ASTMemberValue> flatValue(ASTMemberValue value) {
        return value instanceof ASTMemberValueArrayInitializer
            ? value.children(ASTMemberValue.class)
            : NodeStream.of(value);
    }

    /**
     * Returns the value of the attribute with the given name, returns
     * null if no such attribute was mentioned. For example, for the attribute
     * named "value":
     * <pre>{@code
     * - @SuppressWarnings -> returns null
     * - @SuppressWarning("fallthrough") -> returns "fallthrough"
     * - @SuppressWarning(value={"fallthrough"}) -> returns {"fallthrough"}
     * - @SuppressWarning({"fallthrough", "rawtypes"}) -> returns {"fallthrough", "rawtypes"}
     * }</pre>
     *
     * @param attrName Name of an attribute
     */
    public @Nullable ASTMemberValue getAttribute(String attrName) {
        return getMembers().filter(pair -> pair.getName().equals(attrName))
                           .map(ASTMemberValuePair::getValue)
                           .first();
    }


    @Override
    public <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

}
