/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;

import java.util.List;

import net.sourceforge.pmd.lang.apex.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.apex.ast.ASTAnnotationParameter;
import net.sourceforge.pmd.lang.apex.ast.ASTModifierNode;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.reporting.AbstractAnnotationSuppressor;
import net.sourceforge.pmd.reporting.ViolationSuppressor;

/**
 * @since 7.14.0
 */
final class ApexAnnotationSuppressor extends AbstractAnnotationSuppressor<ASTAnnotation> {

    static final List<ViolationSuppressor> ALL_APEX_SUPPRESSORS = listOf(new ApexAnnotationSuppressor());

    private ApexAnnotationSuppressor() {
        super(ASTAnnotation.class);
    }


    @Override
    protected NodeStream<ASTAnnotation> getAnnotations(Node node) {
        return node.children(ASTModifierNode.class).children(ASTAnnotation.class);
    }

    @Override
    protected boolean walkAnnotation(ASTAnnotation annot, AnnotationWalkCallbacks callbacks) {
        if ("SuppressWarnings".equalsIgnoreCase(annot.getName())) {
            for (ASTAnnotationParameter param : annot.children(ASTAnnotationParameter.class)) {
                String image = param.getValue();

                if (image != null) {
                    for (String part : image.split(",")) {
                        if (callbacks.processNode(param, part)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }

}
