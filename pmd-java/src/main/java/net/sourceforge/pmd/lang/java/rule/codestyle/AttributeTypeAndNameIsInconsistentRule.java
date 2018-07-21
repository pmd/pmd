/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class AttributeTypeAndNameIsInconsistentRule extends AbstractJavaRule {
    private static final Set<String> PREFIXES;

    static {
        final Set<String> prefixCollection = new HashSet<String>();
        prefixCollection.add("is");
        prefixCollection.add("has");
        prefixCollection.add("can");
        prefixCollection.add("have");
        prefixCollection.add("will");
        prefixCollection.add("should");
        PREFIXES = Collections.unmodifiableSet(prefixCollection);
    }

    public AttributeTypeAndNameIsInconsistentRule() {
        addRuleChainVisit(ASTFieldDeclaration.class);
        addRuleChainVisit(ASTLocalVariableDeclaration.class);
    }

    private void checkField(String nameOfField, Node node, Object data) {
        ASTType type = node.getFirstChildOfType(ASTType.class);
        if (type != null) {
            for (String prefix : PREFIXES) {
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
