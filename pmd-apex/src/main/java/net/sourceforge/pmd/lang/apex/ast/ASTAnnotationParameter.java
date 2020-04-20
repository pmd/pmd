/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.annotation.InternalApi;

import apex.jorje.semantic.ast.modifier.AnnotationParameter;

public class ASTAnnotationParameter extends AbstractApexNode<AnnotationParameter> {
    public static final String SEE_ALL_DATA = "seeAllData";

    @Deprecated
    @InternalApi
    public ASTAnnotationParameter(AnnotationParameter annotationParameter) {
        super(annotationParameter);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public String getName() {
        if (node.getProperty() != null) {
            return node.getProperty().getName();
        }
        return null;
    }

    public String getValue() {
        if (node.getValue() != null) {
            return node.getValueAsString();
        }
        return null;
    }

    public Boolean getBooleanValue() {
        return node.getBooleanValue();
    }

    @Override
    public String getImage() {
        return getValue();
    }
}
