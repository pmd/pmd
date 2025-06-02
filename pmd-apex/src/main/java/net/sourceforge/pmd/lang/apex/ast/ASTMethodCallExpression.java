/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.List;
import java.util.stream.Collectors;

import com.google.summit.ast.Identifier;
import com.google.summit.ast.expression.CallExpression;


public final class ASTMethodCallExpression extends AbstractApexNode.Single<CallExpression> {

    /**
     * The {@link Identifier}s that constitute the {@link CallExpression#getReceiver() receiver} of
     * this method call.
     */
    private final List<Identifier> receiverComponents;

    ASTMethodCallExpression(CallExpression callExpression, List<Identifier> receiverComponents) {
        super(callExpression);
        this.receiverComponents = receiverComponents;
    }


    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
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
