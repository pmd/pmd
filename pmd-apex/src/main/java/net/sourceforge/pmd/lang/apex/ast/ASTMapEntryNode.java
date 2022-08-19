/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import com.google.summit.ast.expression.Expression;

public class ASTMapEntryNode extends AbstractApexNode {

    private final Expression key;
    private final Expression value;

    @Deprecated
    @InternalApi
    public ASTMapEntryNode(Expression key, Expression value) {
        super(Void.class);
        this.key = key;
        this.value = value;
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
