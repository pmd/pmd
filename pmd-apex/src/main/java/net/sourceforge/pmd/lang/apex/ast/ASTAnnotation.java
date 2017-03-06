/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import org.apache.commons.lang.StringUtils;

import apex.jorje.semantic.ast.modifier.Annotation;

import net.sourceforge.pmd.Rule;

public class ASTAnnotation extends AbstractApexNode<Annotation> {

    public ASTAnnotation(Annotation annotation) {
        super(annotation);
    }

    public Object jjtAccept(ApexParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    @Override
    public String getImage() {
        return StringUtils.substringBetween(node.toString(), "value = ", "), parameters");
    }

    public boolean suppresses(Rule rule) {
        final String ruleAnno = "PMD." + rule.getName();
        
        if (hasImageEqualTo("SuppressWarnings")) {
        	for(ASTAnnotationParameter param : findChildrenOfType(ASTAnnotationParameter.class)) {
                if(param.hasImageEqualTo("PMD") || param.hasImageEqualTo(ruleAnno) || param.hasImageEqualTo("all")) {
                    return true;
                }
	        }
        }
        
        return false;
    }
}
