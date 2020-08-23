/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.modifier.AnnotationParameter;

public final class ASTAnnotationParameter extends AbstractApexNode<AnnotationParameter> {
    public static final String SEE_ALL_DATA = "seeAllData";

    ASTAnnotationParameter(AnnotationParameter annotationParameter) {
        super(annotationParameter);
    }


    @Override
    protected <P, R> R acceptApexVisitor(ApexVisitor<? super P, ? extends R> visitor, P data) {
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
