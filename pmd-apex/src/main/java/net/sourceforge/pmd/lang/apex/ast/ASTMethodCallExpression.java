/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.Node;
import java.util.Iterator;

import net.sourceforge.pmd.annotation.InternalApi;

public class ASTMethodCallExpression extends AbstractApexNode<Node> {
    @Deprecated
    @InternalApi
    public ASTMethodCallExpression(Node methodCallExpression) {
        super(methodCallExpression);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public String getMethodName() {
        // return node.getMethodName();
        // TODO(b/239648780)
        return null;
    }

    public String getFullMethodName() {
        /*
        final String methodName = getMethodName();
        StringBuilder typeName = new StringBuilder();
        for (Iterator<Identifier> it = node.getReferenceContext().getNames().iterator(); it.hasNext();) {
            typeName.append(it.next().getValue()).append('.');
        }
        return typeName.toString() + methodName;
         */
        // TODO(b/239648780)
        return null;
    }

    public int getInputParametersSize() {
        // return node.getInputParameters().size();
        // TODO(b/239648780)
        return 0;
    }
}
