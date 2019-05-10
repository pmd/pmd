/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.codestyle;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import net.sourceforge.pmd.lang.apex.ast.ASTParameter;
import net.sourceforge.pmd.properties.PropertyDescriptor;

public class FormalParameterRegexNamingConventionsRule extends AbstractRegexNamingConventionsRule {
    private static final Map<String, String> DESCRIPTOR_TO_DISPLAY_NAME = new HashMap<>();

    private static final String CAMEL_CASE = "[a-z][a-zA-Z0-9]*";

    private static final PropertyDescriptor<Pattern> FINAL_METHOD_PARAMETER_REGEX = prop("finalMethodParameterPattern", "final method parameter",
            DESCRIPTOR_TO_DISPLAY_NAME).defaultValue(CAMEL_CASE).build();

    private static final PropertyDescriptor<Pattern> METHOD_PARAMETER_REGEX = prop("methodParameterPattern", "method parameter",
            DESCRIPTOR_TO_DISPLAY_NAME).defaultValue(CAMEL_CASE).build();

    public FormalParameterRegexNamingConventionsRule() {
        definePropertyDescriptor(FINAL_METHOD_PARAMETER_REGEX);
        definePropertyDescriptor(METHOD_PARAMETER_REGEX);

        addRuleChainVisit(ASTParameter.class);
    }

    @Override
    public Object visit(ASTParameter node, Object data) {
        // classes that extend Exception will contains methods that have parameters with null names
        if (node.getImage() == null) {
            return data;
        }

        if (node.getModifiers().isFinal()) {
            checkMatches(FINAL_METHOD_PARAMETER_REGEX, node, data);
        } else {
            checkMatches(METHOD_PARAMETER_REGEX, node, data);
        }

        return data;
    }

    @Override
    protected String displayName(String name) {
        return DESCRIPTOR_TO_DISPLAY_NAME.get(name);
    }
}
