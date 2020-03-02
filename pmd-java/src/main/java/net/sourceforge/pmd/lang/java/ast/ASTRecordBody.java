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
