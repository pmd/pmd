/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.codestyle;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import net.sourceforge.pmd.lang.apex.ast.ASTField;
import net.sourceforge.pmd.lang.apex.ast.ASTModifierNode;
import net.sourceforge.pmd.lang.apex.ast.ASTProperty;
import net.sourceforge.pmd.properties.PropertyDescriptor;

public class FieldRegexNamingConventionsRule extends AbstractRegexNamingConventionsRule {
    private static final Map<String, String> DESCRIPTOR_TO_DISPLAY_NAME = new HashMap<>();

    private static final String CAMEL_CASE = "[a-z][a-zA-Z0-9]*";

    private static final PropertyDescriptor<Pattern> CONSTANT_REGEX = prop("constantPattern", "constant field",
            DESCRIPTOR_TO_DISPLAY_NAME).defaultValue(CAMEL_CASE).build();

    private static final PropertyDescriptor<Pattern> FINAL_REGEX = prop("finalPattern", "final field",
            DESCRIPTOR_TO_DISPLAY_NAME).defaultValue(CAMEL_CASE).build();

    private static final PropertyDescriptor<Pattern> STATIC_REGEX = prop("staticPattern", "static field",
            DESCRIPTOR_TO_DISPLAY_NAME).defaultValue(CAMEL_CASE).build();

    private static final PropertyDescriptor<Pattern> INSTANCE_REGEX = prop("instancePattern", "instance field",
            DESCRIPTOR_TO_DISPLAY_NAME).defaultValue(CAMEL_CASE).build();

    public FieldRegexNamingConventionsRule() {
        definePropertyDescriptor(CONSTANT_REGEX);
        definePropertyDescriptor(FINAL_REGEX);
        definePropertyDescriptor(STATIC_REGEX);
        definePropertyDescriptor(INSTANCE_REGEX);

        addRuleChainVisit(ASTField.class);
    }

    @Override
    public Object visit(ASTField node, Object data) {
        if (node.getFirstParentOfType(ASTProperty.class) != null) {
            return data;
        }

        ASTModifierNode modifiers = node.getModifiers();

        if (modifiers.isFinal() && modifiers.isStatic()) {
            checkMatches(CONSTANT_REGEX, node, data);
        } else if (modifiers.isFinal()) {
            checkMatches(FINAL_REGEX, node, data);
        } else if (modifiers.isStatic()) {
            checkMatches(STATIC_REGEX, node, data);
        } else {
            checkMatches(INSTANCE_REGEX, node, data);
        }

        return data;
    }

    @Override
    protected String displayName(String name) {
        return DESCRIPTOR_TO_DISPLAY_NAME.get(name);
    }
}
