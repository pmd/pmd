/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.modifier.Annotation;

public class ASTAnnotation extends AbstractApexNode<Annotation> {

    public ASTAnnotation(Annotation annotation) {
        super(annotation);
    }

    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }
}
