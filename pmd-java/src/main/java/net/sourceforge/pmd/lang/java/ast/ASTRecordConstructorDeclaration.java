/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.Experimental;

/**
 * This defines a compact constructor for a {@linkplain ASTRecordDeclaration RecordDeclaration} (JDK 14 preview feature).
 *
 * <pre class="grammar">
 *
 * RecordConstructorDeclaration ::=  ({@linkplain ASTAnnotation TypeAnnotation})*
 *                                   {@linkplain ASTModifierList Modifiers}
 *                                   {@linkplain ASTTypeParameters TypeParameters}?
 *                                   &lt;IDENTIFIER&gt;
 *                                   ( "throws" {@linkplain ASTNameList NameList} )?
 *                                   "{" ( {@linkplain ASTBlockStatement ASTBlockStatement} )* "}"
 *
 * </pre>
 *
 */
@Experimental
public final class ASTRecordConstructorDeclaration extends AbstractJavaAccessNode implements ASTAnyTypeBodyDeclaration {
    ASTRecordConstructorDeclaration(int id) {
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

    @Override
    public ASTRecordConstructorDeclaration getDeclarationNode() {
        return this;
    }

    @Override
    public DeclarationKind getKind() {
        return DeclarationKind.RECORD_CONSTRUCTOR;
    }
}
