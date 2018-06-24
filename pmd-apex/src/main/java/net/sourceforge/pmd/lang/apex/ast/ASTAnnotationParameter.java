/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.modifier.AnnotationParameter;

public class ASTAnnotationParameter extends AbstractApexNode<AnnotationParameter> {

    public ASTAnnotationParameter(AnnotationParameter annotationParameter) {
        super(annotationParameter);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public String getImage() {
        if (node.getValue() != null) {
            return node.getValueAsString();
        }
        return null;
    }
}
