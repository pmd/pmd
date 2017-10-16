/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sourceforge.pmd.Rule;

import apex.jorje.semantic.ast.modifier.Annotation;

public class ASTAnnotation extends AbstractApexNode<Annotation> {

    private static final Pattern IMAGE_EXTRACTOR = Pattern.compile("value = ([^\\)]*)\\)");

    public ASTAnnotation(Annotation annotation) {
        super(annotation);
    }

    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public String getImage() {
        final Matcher m = IMAGE_EXTRACTOR.matcher(node.toString());
        if (m.find()) {
            return m.group(1);
        }
        return null;
    }

    public boolean suppresses(Rule rule) {
        final String ruleAnno = "PMD." + rule.getName();

        if (hasImageEqualTo("SuppressWarnings")) {
            for (ASTAnnotationParameter param : findChildrenOfType(ASTAnnotationParameter.class)) {
                String image = param.getImage();

                if (image != null) {
                    Set<String> paramValues = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
                    paramValues.addAll(Arrays.asList(image.replaceAll("\\s+", "").split(",")));
                    if (paramValues.contains("PMD") || paramValues.contains(ruleAnno) || paramValues.contains("all")) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
