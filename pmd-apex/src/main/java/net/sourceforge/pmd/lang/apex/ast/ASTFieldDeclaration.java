/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.Node;
import net.sourceforge.pmd.annotation.InternalApi;

public class ASTFieldDeclaration extends AbstractApexNode.Single<Node> {

    @Deprecated
    @InternalApi
    public ASTFieldDeclaration(Node fieldDeclaration) {
        super(fieldDeclaration);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public String getImage() {
        return getName();
    }

    public String getName() {
        /*
        if (node.getFieldInfo() != null) {
            return node.getFieldInfo().getName();
        }
        ASTVariableExpression variable = getFirstChildOfType(ASTVariableExpression.class);
        if (variable != null) {
            return variable.getImage();
        }
         */
        // TODO(b/239648780)
        return null;
    }
}
