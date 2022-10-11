/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.List;
import java.util.stream.Collectors;

import net.sourceforge.pmd.annotation.InternalApi;

import com.google.summit.ast.Identifier;
import com.google.summit.ast.expression.CallExpression;

public class ASTMethodCallExpression extends AbstractApexNode.Single<CallExpression> {

    /**
     * The {@link Identifier}s that constitute the {@link CallExpression#getReceiver() receiver} of
     * this method call.
     */
    private final List<Identifier> receiverComponents;

    @Deprecated
    @InternalApi
    public ASTMethodCallExpression(CallExpression callExpression, List<Identifier> receiverComponents) {
        super(callExpression);
        this.receiverComponents = receiverComponents;
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public String getMethodName() {
        return node.getId().getString();
    }

    public String getFullMethodName() {
        return receiverComponents.stream().map(id -> id.getString() + ".").collect(Collectors.joining()) + getMethodName();
    }

    public int getInputParametersSize() {
        return node.getArgs().size();
    }
}
