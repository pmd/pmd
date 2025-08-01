/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.codestyle;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTProperty;
import net.sourceforge.pmd.lang.apex.ast.ASTUserEnum;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;
import net.sourceforge.pmd.properties.PropertyDescriptor;

public class MethodNamingConventionsRule extends AbstractNamingConventionsRule {
    private static final Map<String, String> DESCRIPTOR_TO_DISPLAY_NAME = new HashMap<>();

    private static final PropertyDescriptor<Pattern> TEST_REGEX = prop("testPattern", "test method",
            DESCRIPTOR_TO_DISPLAY_NAME).defaultValue(CAMEL_CASE).build();

    private static final PropertyDescriptor<Pattern> STATIC_REGEX = prop("staticPattern", "static method",
            DESCRIPTOR_TO_DISPLAY_NAME).defaultValue(CAMEL_CASE).build();

    private static final PropertyDescriptor<Pattern> INSTANCE_REGEX = prop("instancePattern", "instance method",
            DESCRIPTOR_TO_DISPLAY_NAME).defaultValue(CAMEL_CASE).build();

    public MethodNamingConventionsRule() {
        definePropertyDescriptor(TEST_REGEX);
        definePropertyDescriptor(STATIC_REGEX);
        definePropertyDescriptor(INSTANCE_REGEX);
    }


    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(ASTMethod.class);
    }


    @Override
    public Object visit(ASTMethod node, Object data) {
        if (isOverriddenMethod(node) || isPropertyAccessor(node) || isConstructor(node)) {
            return data;
        }

        if (node.isStaticInitializer()) {
            return data;
        }

        if (node.ancestors(ASTUserEnum.class).first() != null) {
            return data;
        }

        if (node.getModifiers().isTest()) {
            checkMatches(TEST_REGEX, node, data);
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
        return node.ancestors(ASTProperty.class).nonEmpty();
    }

    private boolean isConstructor(ASTMethod node) {
        return node.isConstructor();
    }
}
