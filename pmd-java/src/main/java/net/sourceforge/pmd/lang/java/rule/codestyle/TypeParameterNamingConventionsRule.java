/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import java.util.regex.Pattern;

import net.sourceforge.pmd.lang.java.ast.ASTTypeParameter;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.reporting.RuleContext;


/**
 * Configurable naming conventions for type parameters.
 */
public class TypeParameterNamingConventionsRule extends AbstractNamingConventionRule<ASTTypeParameter> {

    private final PropertyDescriptor<Pattern> typeParameterRegex = defaultProp("typeParameterName", "type parameter").build();

    public TypeParameterNamingConventionsRule() {
        super(ASTTypeParameter.class);
        definePropertyDescriptor(typeParameterRegex);
    }

    @Override
    public Object visit(ASTTypeParameter node, Object data) {
        RuleContext ctx = (RuleContext) data;
        checkMatches(node, typeParameterRegex, ctx);
        return null;
    }

    @Override
    String defaultConvention() {
        return "[A-Z]";
    }

    @Override
    String nameExtractor(ASTTypeParameter node) {
        return node.getName();
    }

    @Override
    String kindDisplayName(ASTTypeParameter node, PropertyDescriptor<Pattern> descriptor) {
        return "type parameter";
    }
}
