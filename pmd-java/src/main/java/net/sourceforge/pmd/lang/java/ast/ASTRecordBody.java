/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.ast;

/**
 * Defines the body of a {@linkplain ASTRecordDeclaration RecordDeclaration} (JDK 16 feature).
 * This can contain additional methods and or constructors.
 *
 * <pre class="grammar">
 *
 * RecordBody ::= "{" (   {@linkplain ASTCompactConstructorDeclaration CompactConstructorDeclaration}
 *                      | {@linkplain ASTClassOrInterfaceBodyDeclaration ClassOrInterfaceBodyDeclaration} )* "}"
 *
 * </pre>
 *
 */
public final class ASTRecordBody extends AbstractJavaNode {
    ASTRecordBody(int id) {
        super(id);
    }

    ASTRecordBody(JavaParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
