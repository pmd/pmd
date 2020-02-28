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
 * RecordConstructorDeclaration ::=  ({@linkplain ASTTypeAnnotation TypeAnnotation})*
 *                                   {@linkplain ASTModifiers Modifiers}
 *                                   {@linkplain ASTTypeParameters TypeParameters}?
 *                                   {@linkplain ASTName Name}
 *                                   ( "throws" {@linkplain ASTNameList NameList} )?
 *                                   "{" ( {@linkplain ASTBlockStatement ASTBlockStatement} )* "}"
 *
 * </pre>
 *
 */
@Experimental
public class ASTRecordConstructorDeclaration extends AbstractJavaAccessNode {
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
}
