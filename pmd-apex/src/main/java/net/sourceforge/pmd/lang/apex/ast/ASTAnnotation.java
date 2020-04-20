/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.annotation.InternalApi;

import apex.jorje.semantic.ast.modifier.Annotation;

public class ASTAnnotation extends AbstractApexNode<Annotation> {

    @Deprecated
    @InternalApi
    public ASTAnnotation(Annotation annotation) {
        super(annotation);
    }

    @Override
    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public String getImage() {
        return node.getType().getApexName();
    }

    public boolean suppresses(Rule rule) {
        final String ruleAnno = "PMD." + rule.getName();

        if (hasImageEqualTo("SuppressWarnings")) {
            for (ASTAnnotationParameter param : findChildrenOfType(ASTAnnotationParameter.class)) {
                String image = param.getImage();

                if (image != null) {
                    Set<String> paramValues = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
                    paramValues.addAll(Arrays.asList(image.replaceAll("\\s+", "").split(",")));
                    if (paramValues.contains("PMD") || paramValues.contains(ruleAnno) || paramValues.contains("all")) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    public boolean isResolved() {
        return node.getType().isResolved();
    }
}
