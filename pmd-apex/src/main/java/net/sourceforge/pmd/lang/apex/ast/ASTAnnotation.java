/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.List;

import apex.jorje.semantic.ast.modifier.Annotation;
import net.sourceforge.pmd.Rule;

public class ASTAnnotation extends AbstractApexNode<Annotation> {

    public ASTAnnotation(Annotation annotation) {
        super(annotation);
    }

    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public boolean suppresses(Rule rule) {
        final String ruleAnno = "'PMD." + rule.getName() + "'";
        ApexNode<?> self = (ApexNode<?>) jjtGetChild(0);

        if ("SuppressWarnings".equals(getImage())) {
            List<ASTAnnotationParameter> nodes = self.findDescendantsOfType(ASTAnnotationParameter.class);
            for (ASTAnnotationParameter element : nodes) {
                if (element.hasImageEqualTo("'PMD'") || element.hasImageEqualTo(ruleAnno) || element.hasImageEqualTo("'all'")) {
                    return true;
                }
            }
        }
        
        return false;
    }
}
