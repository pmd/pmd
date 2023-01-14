/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.Arrays;

import com.google.summit.ast.expression.Expression;

public class ASTMapEntryNode extends AbstractApexNode.Many<Expression> {

    private final Expression key;
    private final Expression value;

    ASTMapEntryNode(Expression key, Expression value) {
        super(Arrays.asList(key, value));
        this.key = key;
        this.value = value;
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public String getImage() {
        return String.format("%s: %s", key, value);
    }
}
