/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.Node;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.annotation.InternalApi;

public class ASTVariableDeclaration extends AbstractApexNode.Single<Node> implements CanSuppressWarnings {

    @Deprecated
    @InternalApi
    public ASTVariableDeclaration(Node variableDeclaration) {
        super(variableDeclaration);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public String getImage() {
        /*
        if (node.getLocalInfo() != null) {
            return node.getLocalInfo().getName();
        }
         */
        // TODO(b/239648780)
        return null;
    }

    @Override
    public boolean hasSuppressWarningsAnnotationFor(Rule rule) {
        ASTVariableDeclarationStatements parent = (ASTVariableDeclarationStatements) getParent();

        for (ASTModifierNode modifier : parent.findChildrenOfType(ASTModifierNode.class)) {
            for (ASTAnnotation a : modifier.findChildrenOfType(ASTAnnotation.class)) {
                if (a.suppresses(rule)) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getType() {
        /*
        if (node.getLocalInfo() != null) {
            return node.getLocalInfo().getType().getApexName();
        }
         */
        // TODO(b/239648780)
        return null;
    }
}
