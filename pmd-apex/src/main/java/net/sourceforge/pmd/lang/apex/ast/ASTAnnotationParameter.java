/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.Node;
import net.sourceforge.pmd.annotation.InternalApi;

public class ASTAnnotationParameter extends AbstractApexNode.Single<Node> {
    public static final String SEE_ALL_DATA = "seeAllData";

    @Deprecated
    @InternalApi
    public ASTAnnotationParameter(Node annotationParameter) {
        super(annotationParameter);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public String getName() {
        // if (node.getProperty() != null) {
        //     return node.getProperty().getName();
        // }
        // TODO(b/239648780)
        return null;
    }

    public String getValue() {
        // if (node.getValue() != null) {
        //     return node.getValueAsString();
        // }
        // TODO(b/239648780)
        return null;
    }

    // public Boolean getBooleanValue() {
    //     return node.getBooleanValue();
    // }
    // TODO(b/239648780)

    @Override
    public String getImage() {
        return getValue();
    }
}
