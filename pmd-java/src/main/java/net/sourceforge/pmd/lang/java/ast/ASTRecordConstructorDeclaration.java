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
 * RecordConstructorDeclaration ::=  ({@linkplain ASTAnnotation Annotation})*
 *                                   RecordModifiers
 *                                   &lt;IDENTIFIER&gt;
 *                                   {@link ASTBlock Block}
 *
 * </pre>
 *
 */
@Experimental
public final class ASTRecordConstructorDeclaration extends AbstractJavaAccessNode implements ASTAnyTypeBodyDeclaration {
    ASTRecordConstructorDeclaration(int id) {
        super(id);
    }

    ASTRecordConstructorDeclaration(JavaParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public ASTRecordConstructorDeclaration getDeclarationNode() {
        return this;
    }

    @Override
    public DeclarationKind getKind() {
        return DeclarationKind.RECORD_CONSTRUCTOR;
    }

    public ASTBlock getBody() {
        return getFirstChildOfType(ASTBlock.class);
    }
}
