/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;


/**
 * Update clause of a {@linkplain ASTForStatement for statement}.
 *
 * <pre>
 *
 * ForUpdate ::= {@linkplain ASTStatementExpressionList StatementExpressionList}
 *
 * </pre>
 */
public class ASTForUpdate extends AbstractJavaNode {

    @InternalApi
    @Deprecated
    public ASTForUpdate(int id) {
        super(id);
    }


    @InternalApi
    @Deprecated
    public ASTForUpdate(JavaParser p, int id) {
        super(p, id);
    }


    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
