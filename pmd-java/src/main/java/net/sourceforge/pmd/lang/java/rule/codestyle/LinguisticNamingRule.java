/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTResultType;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.properties.BooleanProperty;
import net.sourceforge.pmd.properties.StringMultiProperty;

public class LinguisticNamingRule extends AbstractJavaRule {
    private static final BooleanProperty CHECK_BOOLEAN_METHODS = BooleanProperty.named("checkBooleanMethod")
            .defaultValue(true).desc("Check method names and types for inconsistent naming.").uiOrder(1.0f).build();
    private static final BooleanProperty CHECK_GETTERS = BooleanProperty.named("checkGetters").defaultValue(true)
            .desc("Check return type of getters.").uiOrder(2.0f).build();
    private static final BooleanProperty CHECK_SETTERS = BooleanProperty.named("checkSetters").defaultValue(true)
            .desc("Check return type of setters.").uiOrder(3.0f).build();
    private static final BooleanProperty CHECK_PREFIXED_TRANSFORM_METHODS = BooleanProperty
            .named("checkPrefixedTransformMethods").defaultValue(true)
            .desc("Check return type of methods whose names start with the configured prefix (see transformMethodNames property).")
            .uiOrder(4.0f).build();
    private static final BooleanProperty CHECK_TRANSFORM_METHODS = BooleanProperty.named("checkTransformMethods")
            .defaultValue(false)
            .desc("Check return type of methods which contain the configured infix in their name (see transformMethodNames property).")
            .uiOrder(4.0f).build();
    private static final StringMultiProperty BOOLEAN_METHOD_PREFIXES_PROPERTY = StringMultiProperty
            .named("booleanMethodPrefixes").defaultValues("is", "has", "can", "have", "will", "should")
            .desc("The prefixes of methods that return boolean.").uiOrder(5.0f).build();
    private static final StringMultiProperty TRANSFORM_METHOD_NAMES_PROPERTY = StringMultiProperty
            .named("transformMethodNames").defaultValues("to", "as")
            .desc("The prefixes and infixes that indicate a transform method.").uiOrder(6.0f).build();

    private static final BooleanProperty CHECK_FIELDS = BooleanProperty.named("checkFields").defaultValue(true)
            .desc("Check field names and types for inconsistent naming.").uiOrder(7.0f).build();
    private static final BooleanProperty CHECK_VARIABLES = BooleanProperty.named("checkVariables").defaultValue(true)
            .desc("Check local variable names and types for inconsistent naming.").uiOrder(8.0f).build();
    private static final StringMultiProperty BOOLEAN_FIELD_PREFIXES_PROPERTY = StringMultiProperty
            .named("booleanFieldPrefixes").defaultValues("is", "has", "can", "have", "will", "should")
            .desc("The prefixes of fields and variables that indicate boolean.").uiOrder(9.0f).build();

    public LinguisticNamingRule() {
        definePropertyDescriptor(CHECK_BOOLEAN_METHODS);
        definePropertyDescriptor(CHECK_GETTERS);
        definePropertyDescriptor(CHECK_SETTERS);
        definePropertyDescriptor(CHECK_PREFIXED_TRANSFORM_METHODS);
        definePropertyDescriptor(CHECK_TRANSFORM_METHODS);
        definePropertyDescriptor(BOOLEAN_METHOD_PREFIXES_PROPERTY);
        definePropertyDescriptor(TRANSFORM_METHOD_NAMES_PROPERTY);
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

        if (getProperty(CHECK_PREFIXED_TRANSFORM_METHODS)) {
            checkPrefixedTransformMethods(node, data, nameOfMethod);
        }

        if (getProperty(CHECK_TRANSFORM_METHODS)) {
            checkTransformMethods(node, data, nameOfMethod);
        }

        return data;
    }

    private void checkPrefixedTransformMethods(ASTMethodDeclaration node, Object data, String nameOfMethod) {
        ASTResultType resultType = node.getResultType();
        List<String> prefixes = getProperty(TRANSFORM_METHOD_NAMES_PROPERTY);
        String[] splitMethodName = StringUtils.splitByCharacterTypeCamelCase(nameOfMethod);
        if (resultType.isVoid() && splitMethodName.length > 0
                && prefixes.contains(splitMethodName[0].toLowerCase(Locale.ROOT))) {
            // "To" or any other configured prefix found
            addViolationWithMessage(data, node, "Linguistics Antipattern - The transform method ''{0}'' should not return void linguistically",
                    new Object[] { nameOfMethod });
        }
    }

    private void checkTransformMethods(ASTMethodDeclaration node, Object data, String nameOfMethod) {
        ASTResultType resultType = node.getResultType();
        List<String> infixes = getProperty(TRANSFORM_METHOD_NAMES_PROPERTY);
        for (String infix : infixes) {
            if (resultType.isVoid() && containsWord(nameOfMethod, StringUtils.capitalize(infix))) {
                // "To" or any other configured infix in the middle somewhere
                addViolationWithMessage(data, node, "Linguistics Antipattern - The transform method ''{0}'' should not return void linguistically",
                        new Object[] { nameOfMethod });
                // the first violation is sufficient - it is still the same method we are analyzing here
                break;
            }
        }
    }

    private void checkGetters(ASTMethodDeclaration node, Object data, String nameOfMethod) {
        ASTResultType resultType = node.getResultType();
        if (hasPrefix(nameOfMethod, "get") && resultType.isVoid()) {
            addViolationWithMessage(data, node, "Linguistics Antipattern - The getter ''{0}'' should not return void linguistically",
                    new Object[] { nameOfMethod });
        }
    }

    private void checkSetters(ASTMethodDeclaration node, Object data, String nameOfMethod) {
        ASTResultType resultType = node.getResultType();
        if (hasPrefix(nameOfMethod, "set") && !resultType.isVoid()) {
            addViolationWithMessage(data, node, "Linguistics Antipattern - The setter ''{0}'' should not return any type except void linguistically",
                    new Object[] { nameOfMethod });
        }
    }

    private boolean isBooleanType(String typeImage) {
        return "boolean".equalsIgnoreCase(typeImage) || "AtomicBoolean".equals(typeImage);
    }

    private void checkBooleanMethods(ASTMethodDeclaration node, Object data, String nameOfMethod) {
        ASTResultType resultType = node.getResultType();
        ASTType t = node.getResultType().getFirstChildOfType(ASTType.class);
        if (!resultType.isVoid() && t != null) {
            for (String prefix : getProperty(BOOLEAN_METHOD_PREFIXES_PROPERTY)) {
                if (hasPrefix(nameOfMethod, prefix) && !isBooleanType(t.getTypeImage())) {
                    addViolationWithMessage(data, node, "Linguistics Antipattern - The method ''{0}'' indicates linguistically it returns a boolean, but it returns ''{1}''",
                            new Object[] { nameOfMethod, t.getTypeImage() });
                }
            }
        }
    }

    private void checkField(String typeImage, ASTVariableDeclarator node, Object data) {
        for (String prefix : getProperty(BOOLEAN_FIELD_PREFIXES_PROPERTY)) {
            if (hasPrefix(node.getName(), prefix) && !isBooleanType(typeImage)) {
                addViolationWithMessage(data, node, "Linguistics Antipattern - The field ''{0}'' indicates linguistically it is a boolean, but it is ''{1}''",
                        new Object[] { node.getName(), typeImage });
            }
        }
    }

    private void checkVariable(String typeImage, ASTVariableDeclarator node, Object data) {
        for (String prefix : getProperty(BOOLEAN_FIELD_PREFIXES_PROPERTY)) {
            if (hasPrefix(node.getName(), prefix) && !isBooleanType(typeImage)) {
                addViolationWithMessage(data, node, "Linguistics Antipattern - The variable ''{0}'' indicates linguistically it is a boolean, but it is ''{1}''",
                        new Object[] { node.getName(), typeImage });
            }
        }
    }

    @Override
    public Object visit(ASTFieldDeclaration node, Object data) {
        ASTType type = node.getFirstChildOfType(ASTType.class);
        if (type != null && getProperty(CHECK_FIELDS)) {
            List<ASTVariableDeclarator> fields = node.findChildrenOfType(ASTVariableDeclarator.class);
            for (ASTVariableDeclarator field : fields) {
                checkField(type.getTypeImage(), field, data);
            }
        }
        return data;
    }

    @Override
    public Object visit(ASTLocalVariableDeclaration node, Object data) {
        ASTType type = node.getFirstChildOfType(ASTType.class);
        if (type != null && getProperty(CHECK_VARIABLES)) {
            List<ASTVariableDeclarator> variables = node.findChildrenOfType(ASTVariableDeclarator.class);
            for (ASTVariableDeclarator variable : variables) {
                checkVariable(type.getTypeImage(), variable, data);
            }
        }
        return data;
    }

    private static boolean hasPrefix(String name, String prefix) {
        return name.startsWith(prefix) && name.length() > prefix.length()
                && Character.isUpperCase(name.charAt(prefix.length()));
    }

    private static boolean containsWord(String name, String word) {
        int index = name.indexOf(word);
        if (index >= 0 && name.length() > index + word.length()) {
            return Character.isUpperCase(name.charAt(index + word.length()));
        }
        return false;
    }
}
