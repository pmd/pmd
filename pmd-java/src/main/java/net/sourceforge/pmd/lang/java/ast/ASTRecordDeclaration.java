/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.ast;

import java.util.List;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.ast.Node;

/**
 * A record declaration is a special data class type (JDK 14 preview feature).
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
 * @see <a href="https://openjdk.java.net/jeps/359">JEP 359: Records (Preview)</a>
 */
@Experimental
public final class ASTRecordDeclaration extends AbstractAnyTypeDeclaration {
    ASTRecordDeclaration(int id) {
        super(id);
    }

    ASTRecordDeclaration(JavaParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public TypeKind getTypeKind() {
        return TypeKind.RECORD;
    }

    @Override
    public List<ASTAnyTypeBodyDeclaration> getDeclarations() {
        return getFirstChildOfType(ASTRecordBody.class).findChildrenOfType(ASTAnyTypeBodyDeclaration.class);
    }

    @Override
    public boolean isFindBoundary() {
        return isNested();
    }

    /**
     * @deprecated Renamed to {@link #getRecordComponents()}
     */
    @Deprecated
    public ASTRecordComponentList getComponentList() {
        return getRecordComponents();
    }

    /** Returns the record component list. */
    // @NonNull
    @Override
    public ASTRecordComponentList getRecordComponents() {
        return getFirstChildOfType(ASTRecordComponentList.class);
    }
}
