/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */


package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.Experimental;

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
