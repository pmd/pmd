/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.NodeStream;

/**
 * A record declaration is a special data class type (JDK 14 and JDK 15 preview feature).
 * This is a {@linkplain Node#isFindBoundary() find boundary} for tree traversal methods.
 *
 * <pre class="grammar">
 *
 * RecordDeclaration ::= "record"
 *                       &lt;IDENTIFIER&gt;
 *                       {@linkplain ASTTypeParameters TypeParameters}?
 *                       {@linkplain ASTRecordComponentList RecordComponents}
 *                       {@linkplain ASTImplementsList ImplementsList}?
 *                       {@linkplain ASTRecordBody RecordBody}
 *
 * </pre>
 *
 * @see <a href="https://openjdk.java.net/jeps/384">JEP 384: Records (Second Preview)</a>
 */
@Experimental
public final class ASTRecordDeclaration extends AbstractAnyTypeDeclaration {
    ASTRecordDeclaration(int id) {
        super(id);
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    @Override
    public TypeKind getTypeKind() {
        return TypeKind.RECORD;
    }

    @Override
    public NodeStream<ASTAnyTypeBodyDeclaration> getDeclarations() {
        return getFirstChildOfType(ASTRecordBody.class).children(ASTAnyTypeBodyDeclaration.class);
    }

    @Override
    public boolean isFindBoundary() {
        return isNested();
    }

    @Override
    public boolean isFinal() {
        // A record is implicitly final
        return true;
    }

    @Override
    public boolean isLocal() {
        return getParent() instanceof ASTBlockStatement;
    }


    @Override
    public @NonNull ASTRecordComponentList getRecordComponents() {
        return getFirstChildOfType(ASTRecordComponentList.class);
    }
}
