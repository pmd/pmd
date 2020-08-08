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


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

}
