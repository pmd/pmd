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
import net.sourceforge.pmd.lang.apex.ast.ASTUserEnum;
import net.sourceforge.pmd.properties.PropertyDescriptor;

public class FieldNamingConventionsRule extends AbstractNamingConventionsRule {
    private static final Map<String, String> DESCRIPTOR_TO_DISPLAY_NAME = new HashMap<>();

    private static final PropertyDescriptor<Pattern> ENUM_CONSTANT_REGEX = prop("enumConstantPattern", "enum constant field",
            DESCRIPTOR_TO_DISPLAY_NAME).defaultValue(ALL_CAPS).build();

    private static final PropertyDescriptor<Pattern> CONSTANT_REGEX = prop("constantPattern", "constant field",
            DESCRIPTOR_TO_DISPLAY_NAME).defaultValue(ALL_CAPS).build();

    private static final PropertyDescriptor<Pattern> FINAL_REGEX = prop("finalPattern", "final field",
            DESCRIPTOR_TO_DISPLAY_NAME).defaultValue(CAMEL_CASE).build();

    private static final PropertyDescriptor<Pattern> STATIC_REGEX = prop("staticPattern", "static field",
            DESCRIPTOR_TO_DISPLAY_NAME).defaultValue(CAMEL_CASE).build();

    private static final PropertyDescriptor<Pattern> INSTANCE_REGEX = prop("instancePattern", "instance field",
            DESCRIPTOR_TO_DISPLAY_NAME).defaultValue(CAMEL_CASE).build();

    public FieldNamingConventionsRule() {
        definePropertyDescriptor(ENUM_CONSTANT_REGEX);
        definePropertyDescriptor(CONSTANT_REGEX);
        definePropertyDescriptor(FINAL_REGEX);
        definePropertyDescriptor(STATIC_REGEX);
        definePropertyDescriptor(INSTANCE_REGEX);

        setProperty(CODECLIMATE_CATEGORIES, "Style");
        // Note: x10 as Apex has not automatic refactoring
        setProperty(CODECLIMATE_REMEDIATION_MULTIPLIER, 1);
        setProperty(CODECLIMATE_BLOCK_HIGHLIGHTING, false);

        addRuleChainVisit(ASTField.class);
    }

    @Override
    public Object visit(ASTField node, Object data) {
        if (node.getFirstParentOfType(ASTProperty.class) != null) {
            return data;
        }

        ASTModifierNode modifiers = node.getModifiers();

        if (node.getFirstParentOfType(ASTUserEnum.class) != null) {
            checkMatches(ENUM_CONSTANT_REGEX, node, data);
        } else if (modifiers.isFinal() && modifiers.isStatic()) {
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
