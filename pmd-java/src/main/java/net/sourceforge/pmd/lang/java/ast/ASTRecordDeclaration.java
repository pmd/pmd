/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.ast.Node;

/**
 * A record declaration is a special data class type (JDK 14 preview feature).
 * This is a {@linkplain Node#isFindBoundary() find boundary} for tree traversal methods.
 *
 * <pre class="grammar">
 *
 * RecordDeclaration ::= {@link ASTModifierList ModifierList}
 *                       "record"
 *                       &lt;IDENTIFIER&gt;
 *                       {@linkplain ASTTypeParameters TypeParameters}?
 *                       {@linkplain ASTRecordComponentList RecordComponents}
 *                       {@linkplain ASTImplementsList ImplementsList}?
 *                       {@linkplain ASTRecordBody RecordBody}
 *
 * </pre>
 *
 * @see <a href="https://openjdk.java.net/jeps/359">JEP 359: Records (Preview)</a>
 */
@Experimental
public final class ASTRecordDeclaration extends AbstractAnyTypeDeclaration {
    ASTRecordDeclaration(int id) {
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

    /** Returns the list of record components. */
    public ASTRecordComponentList getRecordComponents() {
        return getFirstChildOfType(ASTRecordComponentList.class);
    }
}
