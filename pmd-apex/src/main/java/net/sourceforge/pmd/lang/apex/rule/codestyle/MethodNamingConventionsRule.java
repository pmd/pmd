/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.codestyle;

import static net.sourceforge.pmd.properties.PropertyFactory.booleanProperty;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTProperty;
import net.sourceforge.pmd.lang.apex.ast.ASTUserEnum;
import net.sourceforge.pmd.properties.PropertyDescriptor;

public class MethodNamingConventionsRule extends AbstractNamingConventionsRule {
    private static final Map<String, String> DESCRIPTOR_TO_DISPLAY_NAME = new HashMap<>();

    private static final PropertyDescriptor<Pattern> TEST_REGEX = prop("testPattern", "test method",
            DESCRIPTOR_TO_DISPLAY_NAME).defaultValue(CAMEL_CASE).build();

    private static final PropertyDescriptor<Pattern> STATIC_REGEX = prop("staticPattern", "static method",
            DESCRIPTOR_TO_DISPLAY_NAME).defaultValue(CAMEL_CASE).build();

    private static final PropertyDescriptor<Pattern> INSTANCE_REGEX = prop("instancePattern", "instance method",
            DESCRIPTOR_TO_DISPLAY_NAME).defaultValue(CAMEL_CASE).build();

    private static final PropertyDescriptor<Boolean> SKIP_TEST_METHOD_UNDERSCORES_DESCRIPTOR
        = booleanProperty("skipTestMethodUnderscores")
              .desc("deprecated! Skip underscores in test methods")
              .defaultValue(false)
              .build();

    public MethodNamingConventionsRule() {
        definePropertyDescriptor(SKIP_TEST_METHOD_UNDERSCORES_DESCRIPTOR);
        definePropertyDescriptor(TEST_REGEX);
        definePropertyDescriptor(STATIC_REGEX);
        definePropertyDescriptor(INSTANCE_REGEX);

        setProperty(CODECLIMATE_CATEGORIES, "Style");
        // Note: x10 as Apex has not automatic refactoring
        setProperty(CODECLIMATE_REMEDIATION_MULTIPLIER, 1);
        setProperty(CODECLIMATE_BLOCK_HIGHLIGHTING, false);

        addRuleChainVisit(ASTMethod.class);
    }

    @Override
    public Object visit(ASTMethod node, Object data) {
        if (isOverriddenMethod(node) || isPropertyAccessor(node) || isConstructor(node)) {
            return data;
        }

        if ("<clinit>".equals(node.getImage()) || "clone".equals(node.getImage())) {
            return data;
        }

        if (node.getFirstParentOfType(ASTUserEnum.class) != null) {
            return data;
        }

        if (node.getModifiers().isTest()) {
            if (getProperty(SKIP_TEST_METHOD_UNDERSCORES_DESCRIPTOR)) {
                checkMatches(TEST_REGEX, CAMEL_CASE_WITH_UNDERSCORES, node, data);
            } else {
                checkMatches(TEST_REGEX, node, data);
            }
        } else if (node.getModifiers().isStatic()) {
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

    private boolean isOverriddenMethod(ASTMethod node) {
        return node.getModifiers().isOverride();
    }

    private boolean isPropertyAccessor(ASTMethod node) {
        return !node.getParentsOfType(ASTProperty.class).isEmpty();
    }

    private boolean isConstructor(ASTMethod node) {
        return node.isConstructor();
    }
}
