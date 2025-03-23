/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.codestyle;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.apex.ast.ASTField;
import net.sourceforge.pmd.lang.apex.ast.ASTProperty;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;
import net.sourceforge.pmd.properties.PropertyDescriptor;

public class PropertyNamingConventionsRule extends AbstractNamingConventionsRule {
    private static final Map<String, String> DESCRIPTOR_TO_DISPLAY_NAME = new HashMap<>();

    private static final PropertyDescriptor<Pattern> STATIC_REGEX = prop("staticPattern", "static property",
            DESCRIPTOR_TO_DISPLAY_NAME).defaultValue(CAMEL_CASE).build();

    private static final PropertyDescriptor<Pattern> INSTANCE_REGEX = prop("instancePattern", "instance property",
            DESCRIPTOR_TO_DISPLAY_NAME).defaultValue(CAMEL_CASE).build();

    public PropertyNamingConventionsRule() {
        definePropertyDescriptor(STATIC_REGEX);
        definePropertyDescriptor(INSTANCE_REGEX);
    }


    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(ASTField.class);
    }


    @Override
    public Object visit(ASTField node, Object data) {
        if (node.ancestors(ASTProperty.class).first() == null) {
            return data;
        }

        if (node.getModifiers().isStatic()) {
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
