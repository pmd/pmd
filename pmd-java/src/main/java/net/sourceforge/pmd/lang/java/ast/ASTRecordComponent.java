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
 * RecordComponent ::= ({@linkplain ASTTypeAnnotation TypeAnnotation})*
 *                     {@linkplain ASTType Type}
 *                     &lt;IDENTIFIER&gt;
 *
 * </pre>
 */
@Experimental
public class ASTRecordComponent extends AbstractJavaNode {
    ASTRecordComponent(int id) {
        super(id);
    }

    ASTRecordComponent(JavaParser p, int id) {
        super(p, id);
    }

    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
