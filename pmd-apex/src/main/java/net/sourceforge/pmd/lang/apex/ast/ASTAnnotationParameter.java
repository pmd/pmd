/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import org.apache.commons.lang.StringUtils;

import apex.jorje.semantic.ast.modifier.AnnotationParameter;

public class ASTAnnotationParameter extends AbstractApexNode<AnnotationParameter> {

    public ASTAnnotationParameter(AnnotationParameter annotationParameter) {
        super(annotationParameter);
    }

    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public String getImage() {
    	Object value = node.getValue();
    	String result = (value != null) ? StringUtils.substringBetween(value.toString(), "value = ", ")") : null;
    	return result;
    }
}
