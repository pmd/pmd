/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.Experimental;

/**
 * Defines the body of a {@linkplain ASTRecordDeclaration RecordDeclaration} (JDK 14 preview feature).
 * This can contain additional methods and or constructors.
 *
 * <pre class="grammar">
 *
 * RecordBody ::= "{" (   {@linkplain ASTRecordConstructorDeclaration RecordConstructorDeclaration}
 *                      | {@linkplain ASTClassOrInterfaceBodyDeclaration ClassOrInterfaceBodyDeclaration} )* "}"
 *
 * </pre>
 *
 */
@Experimental
public final class ASTRecordBody extends AbstractJavaNode implements ASTTypeBody {
    ASTRecordBody(int id) {
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
}
