/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTResultType;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.properties.BooleanProperty;
import net.sourceforge.pmd.properties.StringMultiProperty;

public class LinguisticNamingRule extends AbstractJavaRule {
    private static final BooleanProperty CHECK_BOOLEAN_METHODS = BooleanProperty.named("checkBooleanMethod")
            .defaultValue(true).desc("Check method names and types for inconsistent naming").uiOrder(1.0f).build();
    private static final BooleanProperty CHECK_GETTERS = BooleanProperty.named("checkGetters").defaultValue(true)
            .desc("Check return type of getters").uiOrder(2.0f).build();
    private static final BooleanProperty CHECK_SETTERS = BooleanProperty.named("checkSetters").defaultValue(true)
            .desc("Check return type of setters").uiOrder(3.0f).build();
    private static final BooleanProperty CHECK_TRANSFORM_METHODS = BooleanProperty.named("checkTransformMethods")
            .defaultValue(true).desc("Check return type of transform methods").uiOrder(4.0f).build();
    private static final StringMultiProperty BOOLEAN_METHOD_PREFIXES_PROPERTY = StringMultiProperty
            .named("booleanMethodPrefixes").defaultValues("is", "has", "can", "have", "will", "should")
            .desc("the prefixes of methods that return boolean").uiOrder(5.0f).build();

    private static final BooleanProperty CHECK_FIELDS = BooleanProperty.named("checkFields").defaultValue(true)
            .desc("Check field names and types for inconsistent naming").uiOrder(6.0f).build();
    private static final BooleanProperty CHECK_VARIABLES = BooleanProperty.named("checkVariables").defaultValue(true)
            .desc("Check local variable names and types for inconsistent naming").uiOrder(7.0f).build();
    private static final StringMultiProperty BOOLEAN_FIELD_PREFIXES_PROPERTY = StringMultiProperty
            .named("booleanFieldPrefixes").defaultValues("is", "has", "can", "have", "will", "should")
            .desc("the prefixes of fields that return boolean").uiOrder(8.0f).build();

    public LinguisticNamingRule() {
        definePropertyDescriptor(CHECK_BOOLEAN_METHODS);
        definePropertyDescriptor(CHECK_GETTERS);
        definePropertyDescriptor(CHECK_SETTERS);
        definePropertyDescriptor(CHECK_TRANSFORM_METHODS);
        definePropertyDescriptor(BOOLEAN_METHOD_PREFIXES_PROPERTY);
        definePropertyDescriptor(CHECK_FIELDS);
        definePropertyDescriptor(CHECK_VARIABLES);
        definePropertyDescriptor(BOOLEAN_FIELD_PREFIXES_PROPERTY);
        addRuleChainVisit(ASTMethodDeclaration.class);
        addRuleChainVisit(ASTFieldDeclaration.class);
        addRuleChainVisit(ASTLocalVariableDeclaration.class);
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        String nameOfMethod = node.getMethodName();

        if (getProperty(CHECK_BOOLEAN_METHODS)) {
            checkBooleanMethods(node, data, nameOfMethod);
        }

        if (getProperty(CHECK_SETTERS)) {
            checkSetters(node, data, nameOfMethod);
        }

        if (getProperty(CHECK_GETTERS)) {
            checkGetters(node, data, nameOfMethod);
        }

        if (getProperty(CHECK_TRANSFORM_METHODS)) {
            checkTransformMethods(node, data, nameOfMethod);
        }

        return data;
    }

    private void checkTransformMethods(ASTMethodDeclaration node, Object data, String nameOfMethod) {
        ASTResultType resultType = node.getResultType();
        if (nameOfMethod.contains("To") && resultType.isVoid()) {
            // To in the middle somewhere
            // a transform method shouldn't return void linguistically
            addViolation(data, node);
        } else if (hasPrefix(nameOfMethod, "to") && resultType.isVoid()) {
            // a transform method shouldn't return void linguistically
            addViolation(data, node);
        }
    }

    private void checkGetters(ASTMethodDeclaration node, Object data, String nameOfMethod) {
        ASTResultType resultType = node.getResultType();
        if (hasPrefix(nameOfMethod, "get") && resultType.isVoid()) {
            // get method shouldn't return void linguistically
            addViolation(data, node);
        }
    }

    private void checkSetters(ASTMethodDeclaration node, Object data, String nameOfMethod) {
        ASTResultType resultType = node.getResultType();
        if (hasPrefix(nameOfMethod, "set") && !resultType.isVoid()) {
            // set method shouldn't return any type except void linguistically
            addViolation(data, node);
        }
    }

    private void checkBooleanMethods(ASTMethodDeclaration node, Object data, String nameOfMethod) {
        ASTResultType resultType = node.getResultType();
        ASTType t = node.getResultType().getFirstChildOfType(ASTType.class);
        if (!resultType.isVoid() && t != null) {
            for (String prefix : getProperty(BOOLEAN_METHOD_PREFIXES_PROPERTY)) {
                if (hasPrefix(nameOfMethod, prefix) && !"boolean".equals(t.getType().getName())) {
                    addViolation(data, node);
                }
            }
        }
    }

    private void checkField(String nameOfField, Node node, Object data) {
        ASTType type = node.getFirstChildOfType(ASTType.class);
        if (type != null) {
            for (String prefix : getProperty(BOOLEAN_FIELD_PREFIXES_PROPERTY)) {
                if (hasPrefix(nameOfField, prefix) && !"boolean".equals(type.getType().getName())) {
                    addViolation(data, node);
                }
            }
        }
    }

    @Override
    public Object visit(ASTFieldDeclaration node, Object data) {
        String nameOfField = node.getVariableName();
        if (getProperty(CHECK_FIELDS)) {
            checkField(nameOfField, node, data);
        }
        return data;
    }

    @Override
    public Object visit(ASTLocalVariableDeclaration node, Object data) {
        String nameOfField = node.getVariableName();
        if (getProperty(CHECK_VARIABLES)) {
            checkField(nameOfField, node, data);
        }
        return data;
    }

    private static boolean hasPrefix(String name, String prefix) {
        return name.startsWith(prefix) && name.length() > prefix.length()
                && Character.isUpperCase(name.charAt(prefix.length()));
    }
}
