/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.codestyle;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ASTUserEnum;
import net.sourceforge.pmd.lang.apex.ast.ASTUserInterface;
import net.sourceforge.pmd.properties.PropertyDescriptor;

public class ClassNamingConventionsRule extends AbstractNamingConventionsRule {
    private static final Map<String, String> DESCRIPTOR_TO_DISPLAY_NAME = new HashMap<>();

    private static final PropertyDescriptor<Pattern> TEST_CLASS_REGEX = prop("testClassPattern", "test class",
            DESCRIPTOR_TO_DISPLAY_NAME).defaultValue(PASCAL_CASE_WITH_UNDERSCORES).build();

    private static final PropertyDescriptor<Pattern> ABSTRACT_CLASS_REGEX = prop("abstractClassPattern", "abstract class",
            DESCRIPTOR_TO_DISPLAY_NAME).defaultValue(PASCAL_CASE_WITH_UNDERSCORES).build();

    private static final PropertyDescriptor<Pattern> CLASS_REGEX = prop("classPattern", "class",
            DESCRIPTOR_TO_DISPLAY_NAME).defaultValue(PASCAL_CASE_WITH_UNDERSCORES).build();

    private static final PropertyDescriptor<Pattern> INTERFACE_REGEX = prop("interfacePattern", "interface",
            DESCRIPTOR_TO_DISPLAY_NAME).defaultValue(PASCAL_CASE_WITH_UNDERSCORES).build();

    private static final PropertyDescriptor<Pattern> ENUM_REGEX = prop("enumPattern", "enum",
            DESCRIPTOR_TO_DISPLAY_NAME).defaultValue(PASCAL_CASE_WITH_UNDERSCORES).build();

    public ClassNamingConventionsRule() {
        definePropertyDescriptor(TEST_CLASS_REGEX);
        definePropertyDescriptor(ABSTRACT_CLASS_REGEX);
        definePropertyDescriptor(CLASS_REGEX);
        definePropertyDescriptor(INTERFACE_REGEX);
        definePropertyDescriptor(ENUM_REGEX);

        setProperty(CODECLIMATE_CATEGORIES, "Style");
        // Note: x10 as Apex has not automatic refactoring
        setProperty(CODECLIMATE_REMEDIATION_MULTIPLIER, 5);
        setProperty(CODECLIMATE_BLOCK_HIGHLIGHTING, false);

        addRuleChainVisit(ASTUserClass.class);
        addRuleChainVisit(ASTUserInterface.class);
        addRuleChainVisit(ASTUserEnum.class);
    }

    @Override
    public Object visit(ASTUserClass node, Object data) {
        if (node.getModifiers().isTest()) {
            checkMatches(TEST_CLASS_REGEX, node, data);
        } else if (node.getModifiers().isAbstract()) {
            checkMatches(ABSTRACT_CLASS_REGEX, node, data);
        } else {
            checkMatches(CLASS_REGEX, node, data);
        }

        return data;
    }

    @Override
    public Object visit(ASTUserInterface node, Object data) {
        checkMatches(INTERFACE_REGEX, node, data);

        return data;
    }

    @Override
    public Object visit(ASTUserEnum node, Object data) {
        checkMatches(ENUM_REGEX, node, data);

        return data;
    }

    @Override
    protected String displayName(String name) {
        return DESCRIPTOR_TO_DISPLAY_NAME.get(name);
    }
}
