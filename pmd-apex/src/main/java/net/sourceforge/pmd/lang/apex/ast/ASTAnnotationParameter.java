/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import apex.jorje.semantic.ast.modifier.AnnotationParameter;

public class ASTAnnotationParameter extends AbstractApexNode<AnnotationParameter> {

    private static final Pattern IMAGE_EXTRACTOR = Pattern.compile("value = ([^\\)]*)\\)");

    public ASTAnnotationParameter(AnnotationParameter annotationParameter) {
        super(annotationParameter);
    }

    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public String getImage() {
        if (node.getValue() != null) {
            final Matcher m = IMAGE_EXTRACTOR.matcher(node.getValue().toString());
            if (m.find()) {
                return m.group(1);
            }
        }

        return null;
    }
}
