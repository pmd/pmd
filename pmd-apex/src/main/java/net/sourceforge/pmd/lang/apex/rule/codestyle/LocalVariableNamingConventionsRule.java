/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.codestyle;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import net.sourceforge.pmd.lang.apex.ast.ASTVariableDeclaration;
import net.sourceforge.pmd.lang.apex.ast.ASTVariableDeclarationStatements;
import net.sourceforge.pmd.properties.PropertyDescriptor;

public class LocalVariableNamingConventionsRule extends AbstractNamingConventionsRule {
    private static final Map<String, String> DESCRIPTOR_TO_DISPLAY_NAME = new HashMap<>();

    private static final PropertyDescriptor<Pattern> FINAL_REGEX = prop("finalLocalPattern", "final local variable",
            DESCRIPTOR_TO_DISPLAY_NAME).defaultValue(CAMEL_CASE).build();

    private static final PropertyDescriptor<Pattern> LOCAL_REGEX = prop("localPattern", "local variable",
            DESCRIPTOR_TO_DISPLAY_NAME).defaultValue(CAMEL_CASE).build();

    public LocalVariableNamingConventionsRule() {
        definePropertyDescriptor(FINAL_REGEX);
        definePropertyDescriptor(LOCAL_REGEX);

        setProperty(CODECLIMATE_CATEGORIES, "Style");
        // Note: x10 as Apex has not automatic refactoring
        setProperty(CODECLIMATE_REMEDIATION_MULTIPLIER, 1);
        setProperty(CODECLIMATE_BLOCK_HIGHLIGHTING, false);

        addRuleChainVisit(ASTVariableDeclaration.class);
    }

    @Override
    public Object visit(ASTVariableDeclaration node, Object data) {
        if (node.getFirstParentOfType(ASTVariableDeclarationStatements.class).getModifiers().isFinal()) {
            checkMatches(FINAL_REGEX, node, data);
        } else {
            checkMatches(LOCAL_REGEX, node, data);
        }

        return data;
    }

    @Override
    protected String displayName(String name) {
        return DESCRIPTOR_TO_DISPLAY_NAME.get(name);
    }
}
