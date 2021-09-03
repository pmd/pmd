/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.NodeStream;

/**
 * A record declaration is a special data class type (JDK 16 feature).
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
 * @see <a href="https://openjdk.java.net/jeps/395">JEP 395: Records</a>
 */
public final class ASTRecordDeclaration extends AbstractAnyTypeDeclaration {
    ASTRecordDeclaration(int id) {
        super(id);
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    @Override
    public NodeStream<ASTBodyDeclaration> getDeclarations() {
        return getFirstChildOfType(ASTRecordBody.class).children(ASTBodyDeclaration.class);
    }

    @Override
    public boolean isFindBoundary() {
        return isNested();
    }

    @Override
    @NonNull
    public ASTRecordComponentList getRecordComponents() {
        return getFirstChildOfType(ASTRecordComponentList.class);
    }
}
