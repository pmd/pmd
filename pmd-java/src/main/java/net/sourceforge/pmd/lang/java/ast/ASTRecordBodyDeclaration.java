/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.Experimental;

/**
 * This is part of {@linkplain ASTRecordDeclaration RecordDeclaration} (JDK 14 preview feature).
 * It is can contain either a normal method or constructor or a compact
 * {@linkplain ASTRecordConstructorDeclaration RecordConstructorDeclaration}.
 *
 * <pre class="grammar">
 *
 * RecordBodyDeclaration ::=   {@linkplain ASTRecordConstructorDeclaration RecordConstructorDeclaration}
 *                           | {@linkplain ASTClassOrInterfaceBodyDeclaration ClassOrInterfaceBodyDeclaration}
 *
 * </pre>
 *
 */
@Experimental
public class ASTRecordBodyDeclaration extends AbstractJavaNode {
    ASTRecordBodyDeclaration(int id) {
        super(id);
    }

    ASTRecordBodyDeclaration(JavaParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
