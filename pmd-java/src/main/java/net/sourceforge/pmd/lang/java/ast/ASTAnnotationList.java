/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Iterator;


/**
 * A list of annotations. This can be found in {@link ASTType types},
 * or declarations.
 *
 * <p>Other modifiers may separate the annotations, e.g. in {@code @Foo public @Bar class AClass {}}.
 * In both of these cases, there's no other node between the annotations anyway
 *
 * <pre class="grammar">
 *
 * AnnotationList ::= {@link ASTAnnotation Annotation}+
 *
 * </pre>
 */
public final class ASTAnnotationList extends AbstractJavaNode implements Iterable<ASTAnnotation> {

    ASTAnnotationList(int id) {
        super(id);
    }

    ASTAnnotationList(JavaParser p, int id) {
        super(p, id);
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
     * Returns the number of annotations in this list.
     */
    public int getCount() {
        return jjtGetNumChildren();
    }

    @Override
    public Iterator<ASTAnnotation> iterator() {
        return new NodeChildrenIterator<>(this, ASTAnnotation.class);
    }
}
