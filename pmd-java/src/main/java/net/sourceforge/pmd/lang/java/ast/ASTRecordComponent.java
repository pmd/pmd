/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.Experimental;

/**
 * Defines a single component of a {@linkplain ASTRecordDeclaration RecordDeclaration} (JDK 14 preview feature).
 *
 * <pre class="grammar">
 *
 * RecordComponent ::= ({@linkplain ASTAnnotation Annotation})*
 *                     {@linkplain ASTType Type}
 *                     ( "..." )?
 *                     {@linkplain ASTVariableDeclaratorId VariableDeclaratorId}
 *
 * </pre>
 */
@Experimental
public final class ASTRecordComponent extends AbstractJavaNode implements AccessNode {

    ASTRecordComponent(int id) {
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

    public boolean isVarargs() {
        return getTypeNode() instanceof ASTArrayType && ((ASTArrayType) getTypeNode()).getDimensions().getLastChild().isVarargs();
    }

    public ASTType getTypeNode() {
        return getFirstChildOfType(ASTType.class);
    }

    public ASTVariableDeclaratorId getVarId() {
        return getFirstChildOfType(ASTVariableDeclaratorId.class);
    }
}
