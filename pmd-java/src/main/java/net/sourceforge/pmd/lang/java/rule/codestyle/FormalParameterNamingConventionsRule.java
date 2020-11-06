/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;


import java.util.regex.Pattern;

import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.properties.PropertyDescriptor;


/**
 * Enforces a naming convention for lambda and method parameters.
 *
 * @author Clément Fournier
 * @since 6.6.0
 */
public final class FormalParameterNamingConventionsRule extends AbstractNamingConventionRule<ASTVariableDeclaratorId> {

    // These are not exhaustive, but are chosen to be the most useful, for a start


    private final PropertyDescriptor<Pattern> formalParamRegex = defaultProp("methodParameter", "formal parameter").build();
    private final PropertyDescriptor<Pattern> finalFormalParamRegex = defaultProp("finalMethodParameter", "final formal parameter").build();

    private final PropertyDescriptor<Pattern> lambdaParamRegex = defaultProp("lambdaParameter", "inferred-type lambda parameter").build();
    private final PropertyDescriptor<Pattern> explicitLambdaParamRegex = defaultProp("explicitLambdaParameter", "explicitly-typed lambda parameter").build();


    public FormalParameterNamingConventionsRule() {
        definePropertyDescriptor(formalParamRegex);
        definePropertyDescriptor(finalFormalParamRegex);
        definePropertyDescriptor(lambdaParamRegex);
        definePropertyDescriptor(explicitLambdaParamRegex);

        addRuleChainVisit(ASTVariableDeclaratorId.class);
    }


    @Override
    public Object visit(ASTVariableDeclaratorId node, Object data) {

        if (node.isLambdaParameter()) {
            checkMatches(node, node.isTypeInferred() ? lambdaParamRegex : explicitLambdaParamRegex, data);
        } else if (node.isFormalParameter()) {
            checkMatches(node, node.isFinal() ? finalFormalParamRegex : formalParamRegex, data);
        }

        return data;
    }


    @Override
    String defaultConvention() {
        return CAMEL_CASE;
    }


    @Override
    String kindDisplayName(ASTVariableDeclaratorId node, PropertyDescriptor<Pattern> descriptor) {
        if (node.isLambdaParameter()) {
            return node.isTypeInferred() ? "lambda parameter" : "explicitly-typed lambda parameter";
        } else if (node.isFormalParameter()) { // necessarily a method parameter here
            return node.isFinal() ? "final method parameter" : "method parameter";
        }

        throw new UnsupportedOperationException("This rule doesn't handle this case");
    }
}
