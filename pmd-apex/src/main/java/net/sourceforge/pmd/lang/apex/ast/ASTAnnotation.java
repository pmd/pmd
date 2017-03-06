/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

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
        String result = StringUtils.substringBetween(node.toString(), "value = ", ")");
        return result;
    }

    public boolean suppresses(Rule rule) {
        final String ruleAnno = "PMD." + rule.getName();
        
        if (hasImageEqualTo("SuppressWarnings")) {
        	for(ASTAnnotationParameter param : findChildrenOfType(ASTAnnotationParameter.class)) {
        		String image = param.getImage();
        		
        		if(image != null) {
        			Set<String> paramValues = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
        			paramValues.addAll(Arrays.asList(image.replaceAll("\\s+","").split(",")));
        			if(paramValues.contains("PMD") || paramValues.contains(ruleAnno) || paramValues.contains("all")) {
        				return true;
        			}
        		}
	        }
        }
        
        return false;
    }
}
