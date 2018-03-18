/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTMemberValuePair;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

/**
 * @author Kirk Clemens
 * @since 6.2.0
 */
public class UnnecessaryAnnotationValueElementRule extends AbstractJavaRule {

    public UnnecessaryAnnotationValueElementRule() {
        addRuleChainVisit(ASTAnnotation.class);
    }

    @Override
    public Object visit(ASTAnnotation node, Object data) {

        final List<ASTMemberValuePair> annotationProperties = node.findDescendantsOfType(ASTMemberValuePair.class);
        // all that needs to be done is check to if there's a single property in the annotation and if if that property is value
        // then it's a violation and it should be resolved.
        if (annotationProperties.size() == 1 && "value".equals(annotationProperties.get(0).getImage())) {
            addViolation(data, node);
        }

        return data;
    }
}
