/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Iterator;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.util.StringUtil;

/**
 * Represents an annotation.
 *
 * <pre class="grammar">
 *
 * Annotation ::= "@" Name {@link ASTAnnotationMemberList AnnotationMemberList}?
 *
 * </pre>
 */
public final class ASTAnnotation extends AbstractJavaTypeNode implements TypeNode, ASTMemberValue, Iterable<ASTMemberValuePair> {

    String name;

    ASTAnnotation(int id) {
        super(id);
    }


    /**
     * Returns the name of the annotation as it is used,
     * eg {@code java.lang.Override} or {@code Override}.
     */
    public String getAnnotationName() {
        return name;
    }

    @Override
    @Deprecated
    public String getImage() {
        return name;
    }

    /**
     * Returns the simple name of the annotation.
     */
    public String getSimpleName() {
        return StringUtil.substringAfterLast(getImage(), '.');
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
    public NodeStream<ASTMemberValue> getValuesForName(String attrName) {
        return getMembers().filter(pair -> pair.getName().equals(attrName))
                           .map(ASTMemberValuePair::getValue)
                           .flatMap(v -> v instanceof ASTMemberValueArrayInitializer ? v.children(ASTMemberValue.class)
                                                                                     : NodeStream.of(v));
    }


    @Override
    public <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

}
