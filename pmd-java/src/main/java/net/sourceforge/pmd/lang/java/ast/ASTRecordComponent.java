/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.java.ast.InternalInterfaces.VariableIdOwner;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JConstructorSymbol;

/**
 * Defines a single component of a {@linkplain ASTRecordDeclaration RecordDeclaration} (JDK 14 preview feature).
 *
 * <p>The varargs ellipsis {@code "..."} is parsed as an {@linkplain ASTArrayTypeDim array dimension}
 * in the type node.
 *
 * <p>Record components declare a field, and if a canonical constructor
 * is synthesized by the compiler, also a formal parameter (which is in
 * scope in the body of a {@linkplain ASTRecordConstructorDeclaration compact record constructor}).
 * They also may imply the declaration of an accessor method.
 * <ul>
 * <li>The symbol exposed by the {@link ASTVariableDeclaratorId} is the field
 * symbol.
 * <li> The formal parameter symbol is accessible in the formal parameter
 * list of the {@link JConstructorSymbol} for the {@linkplain ASTRecordComponentList#getSymbol() canonical constructor}.
 * <li>The symbol for the accessor method can be found in the {@link JClassSymbol#getDeclaredMethods() declared methods}
 * of the symbol for the record declaration. TODO when we support usage search this needs to be more straightforward
 * </ul>
 *
 * <pre class="grammar">
 *
 * RecordComponent ::= {@linkplain ASTAnnotation Annotation}* {@linkplain ASTType Type} {@linkplain ASTVariableDeclaratorId VariableDeclaratorId}
 *
 * </pre>
 */
@Experimental
public final class ASTRecordComponent extends AbstractJavaNode implements AccessNode, VariableIdOwner {

    ASTRecordComponent(int id) {
        super(id);
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }


    /**
     * Returns true if this component's corresponding formal parameter
     * in the canonical constructor of the record is varargs. The type
     * node of this component is in this case an {@link ASTArrayType}.
     */
    public boolean isVarargs() {
        return getTypeNode() instanceof ASTArrayType && ((ASTArrayType) getTypeNode()).getDimensions().getLastChild().isVarargs();
    }

    public ASTType getTypeNode() {
        return getFirstChildOfType(ASTType.class);
    }

    @Override
    public ASTVariableDeclaratorId getVarId() {
        return getFirstChildOfType(ASTVariableDeclaratorId.class);
    }
}
