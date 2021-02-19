/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.ast;

/**
 * This defines a compact constructor for a {@linkplain ASTRecordDeclaration RecordDeclaration} (JDK 16 feature).
 *
 * <pre class="grammar">
 *
 * CompactConstructorDeclaration ::=  ({@linkplain ASTAnnotation Annotation})*
 *                                   RecordModifiers
 *                                   &lt;IDENTIFIER&gt;
 *                                   {@link ASTBlock Block}
 *
 * </pre>
 *
 */
public final class ASTCompactConstructorDeclaration extends AbstractJavaAccessNode implements ASTAnyTypeBodyDeclaration {
    ASTCompactConstructorDeclaration(int id) {
        super(id);
    }

    ASTCompactConstructorDeclaration(JavaParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public ASTCompactConstructorDeclaration getDeclarationNode() {
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
