/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.properties.StringMultiProperty;

public class AttributeTypeAndNameIsInconsistentRule extends AbstractJavaRule {
    private static final StringMultiProperty BOOLEAN_PREFIXES_PROPERTY = StringMultiProperty.named("booleanPrefixes")
            .defaultValues("is", "has", "can", "have", "will", "should")
            .desc("the prefixes of fields that return boolean")
            .uiOrder(1.0f)
            .build();

    public AttributeTypeAndNameIsInconsistentRule() {
        definePropertyDescriptor(BOOLEAN_PREFIXES_PROPERTY);
        addRuleChainVisit(ASTFieldDeclaration.class);
        addRuleChainVisit(ASTLocalVariableDeclaration.class);
    }

    private void checkField(String nameOfField, Node node, Object data) {
        ASTType type = node.getFirstChildOfType(ASTType.class);
        if (type != null) {
            for (String prefix : getProperty(BOOLEAN_PREFIXES_PROPERTY)) {
                if (nameOfField.startsWith(prefix) && nameOfField.length() > prefix.length()
                        && Character.isUpperCase(nameOfField.charAt(prefix.length()))) {
                    if (!"boolean".equals(type.getType().getName())) {
                        addViolation(data, node);
                    }
                }
            }
        }
    }

    @Override
    public Object visit(ASTFieldDeclaration node, Object data) {
        String nameOfField = node.getVariableName();
        checkField(nameOfField, node, data);
        return data;
    }

    @Override
    public Object visit(ASTLocalVariableDeclaration node, Object data) {
        String nameOfField = node.getVariableName();
        checkField(nameOfField, node, data);
        return data;
    }
}
