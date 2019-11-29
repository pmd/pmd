/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import static net.sourceforge.pmd.properties.PropertyFactory.booleanProperty;
import static net.sourceforge.pmd.properties.PropertyFactory.stringListProperty;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTResultType;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.rule.AbstractIgnoredAnnotationRule;
import net.sourceforge.pmd.lang.java.typeresolution.TypeHelper;
import net.sourceforge.pmd.properties.PropertyDescriptor;

public class LinguisticNamingRule extends AbstractIgnoredAnnotationRule {
    private static final PropertyDescriptor<Boolean> CHECK_BOOLEAN_METHODS =
            booleanProperty("checkBooleanMethod").defaultValue(true).desc("Check method names and types for inconsistent naming.").build();
    private static final PropertyDescriptor<Boolean> CHECK_GETTERS =
            booleanProperty("checkGetters").defaultValue(true).desc("Check return type of getters.").build();
    private static final PropertyDescriptor<Boolean> CHECK_SETTERS =
            booleanProperty("checkSetters").defaultValue(true).desc("Check return type of setters.").build();
    private static final PropertyDescriptor<Boolean> CHECK_PREFIXED_TRANSFORM_METHODS =
            booleanProperty("checkPrefixedTransformMethods")
                    .desc("Check return type of methods whose names start with the configured prefix (see transformMethodNames property).")
                    .defaultValue(true).build();
    private static final PropertyDescriptor<Boolean> CHECK_TRANSFORM_METHODS =
            booleanProperty("checkTransformMethods")
                    .desc("Check return type of methods which contain the configured infix in their name (see transformMethodNames property).")
                    .defaultValue(false).build();
    private static final PropertyDescriptor<Boolean> CHECK_FIELDS =
            booleanProperty("checkFields").defaultValue(true).desc("Check field names and types for inconsistent naming.").build();
    private static final PropertyDescriptor<Boolean> CHECK_VARIABLES =
            booleanProperty("checkVariables").defaultValue(true).desc("Check local variable names and types for inconsistent naming.").build();
    private static final PropertyDescriptor<List<String>> BOOLEAN_METHOD_PREFIXES_PROPERTY =
            stringListProperty("booleanMethodPrefixes")
                    .desc("The prefixes of methods that return boolean.")
                    .defaultValues("is", "has", "can", "have", "will", "should").build();
    private static final PropertyDescriptor<List<String>> TRANSFORM_METHOD_NAMES_PROPERTY =
            stringListProperty("transformMethodNames")
                    .desc("The prefixes and infixes that indicate a transform method.")
                    .defaultValues("to", "as").build();
    private static final PropertyDescriptor<List<String>> BOOLEAN_FIELD_PREFIXES_PROPERTY =
            stringListProperty("booleanFieldPrefixes")
                    .desc("The prefixes of fields and variables that indicate boolean.")
                    .defaultValues("is", "has", "can", "have", "will", "should").build();

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
    protected Collection<String> defaultSuppressionAnnotations() {
        return Collections.checkedList(Arrays.asList("java.lang.Override"), String.class);
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        if (!hasIgnoredAnnotation(node)) {
            String nameOfMethod = node.getName();

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

    private boolean isBooleanType(ASTType node) {
        return "boolean".equalsIgnoreCase(node.getTypeImage())
                || TypeHelper.isA(node, "java.util.concurrent.atomic.AtomicBoolean")
                || TypeHelper.isA(node, "java.util.function.Predicate");
    }

    private void checkBooleanMethods(ASTMethodDeclaration node, Object data, String nameOfMethod) {
        ASTResultType resultType = node.getResultType();
        ASTType t = node.getResultType().getFirstChildOfType(ASTType.class);
        if (!resultType.isVoid() && t != null) {
            for (String prefix : getProperty(BOOLEAN_METHOD_PREFIXES_PROPERTY)) {
                if (hasPrefix(nameOfMethod, prefix) && !isBooleanType(t)) {
                    addViolationWithMessage(data, node, "Linguistics Antipattern - The method ''{0}'' indicates linguistically it returns a boolean, but it returns ''{1}''",
                            new Object[] { nameOfMethod, t.getTypeImage() });
                }
            }
        }
    }

    private void checkField(ASTType typeNode, ASTVariableDeclarator node, Object data) {
        for (String prefix : getProperty(BOOLEAN_FIELD_PREFIXES_PROPERTY)) {
            if (hasPrefix(node.getName(), prefix) && !isBooleanType(typeNode)) {
                addViolationWithMessage(data, node, "Linguistics Antipattern - The field ''{0}'' indicates linguistically it is a boolean, but it is ''{1}''",
                        new Object[] { node.getName(), typeNode.getTypeImage() });
            }
        }
    }

    private void checkVariable(ASTType typeNode, ASTVariableDeclarator node, Object data) {
        for (String prefix : getProperty(BOOLEAN_FIELD_PREFIXES_PROPERTY)) {
            if (hasPrefix(node.getName(), prefix) && !isBooleanType(typeNode)) {
                addViolationWithMessage(data, node, "Linguistics Antipattern - The variable ''{0}'' indicates linguistically it is a boolean, but it is ''{1}''",
                        new Object[] { node.getName(), typeNode.getTypeImage() });
            }
        }
    }

    @Override
    public Object visit(ASTFieldDeclaration node, Object data) {
        ASTType type = node.getFirstChildOfType(ASTType.class);
        if (type != null && getProperty(CHECK_FIELDS)) {
            List<ASTVariableDeclarator> fields = node.findChildrenOfType(ASTVariableDeclarator.class);
            for (ASTVariableDeclarator field : fields) {
                checkField(type, field, data);
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
                checkVariable(type, variable, data);
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
