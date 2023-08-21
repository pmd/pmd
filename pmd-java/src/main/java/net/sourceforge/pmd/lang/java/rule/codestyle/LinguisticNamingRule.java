/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import static net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil.containsCamelCaseWord;
import static net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil.startsWithCamelCaseWord;
import static net.sourceforge.pmd.properties.PropertyFactory.booleanProperty;
import static net.sourceforge.pmd.properties.PropertyFactory.stringListProperty;

import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.ast.Annotatable;
import net.sourceforge.pmd.lang.java.ast.internal.PrettyPrintingUtil;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.JavaPropertyUtil;
import net.sourceforge.pmd.lang.java.types.JPrimitiveType.PrimitiveTypeKind;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.properties.PropertyDescriptor;

public class LinguisticNamingRule extends AbstractJavaRulechainRule {
    private static final PropertyDescriptor<List<String>> IGNORED_ANNOTS =
        JavaPropertyUtil.ignoredAnnotationsDescriptor("java.lang.Override");

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
        super(ASTMethodDeclaration.class, ASTFieldDeclaration.class, ASTLocalVariableDeclaration.class);
        definePropertyDescriptor(IGNORED_ANNOTS);
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

    private boolean hasIgnoredAnnotation(Annotatable node) {
        return node.isAnyAnnotationPresent(getProperty(IGNORED_ANNOTS));
    }

    private void checkPrefixedTransformMethods(ASTMethodDeclaration node, Object data, String nameOfMethod) {
        List<String> prefixes = getProperty(TRANSFORM_METHOD_NAMES_PROPERTY);
        String[] splitMethodName = StringUtils.splitByCharacterTypeCamelCase(nameOfMethod);
        if (node.isVoid() && splitMethodName.length > 0
                && prefixes.contains(splitMethodName[0].toLowerCase(Locale.ROOT))) {
            // "To" or any other configured prefix found
            addViolationWithMessage(data, node, "Linguistics Antipattern - The transform method ''{0}'' should not return void linguistically",
                    new Object[] { nameOfMethod });
        }
    }

    private void checkTransformMethods(ASTMethodDeclaration node, Object data, String nameOfMethod) {
        for (String infix : getProperty(TRANSFORM_METHOD_NAMES_PROPERTY)) {
            if (node.isVoid() && containsCamelCaseWord(nameOfMethod, StringUtils.capitalize(infix))) {
                // "To" or any other configured infix in the middle somewhere
                addViolationWithMessage(data, node, "Linguistics Antipattern - The transform method ''{0}'' should not return void linguistically",
                        new Object[] { nameOfMethod });
                // the first violation is sufficient - it is still the same method we are analyzing here
                break;
            }
        }
    }

    private void checkGetters(ASTMethodDeclaration node, Object data, String nameOfMethod) {
        if (startsWithCamelCaseWord(nameOfMethod, "get") && node.isVoid()) {
            addViolationWithMessage(data, node, "Linguistics Antipattern - The getter ''{0}'' should not return void linguistically",
                    new Object[] { nameOfMethod });
        }
    }

    private void checkSetters(ASTMethodDeclaration node, Object data, String nameOfMethod) {
        if (startsWithCamelCaseWord(nameOfMethod, "set") && !node.isVoid()) {
            addViolationWithMessage(data, node, "Linguistics Antipattern - The setter ''{0}'' should not return any type except void linguistically",
                    new Object[] { nameOfMethod });
        }
    }

    private boolean isBooleanType(ASTType node) {
        return node.getTypeMirror().unbox().isPrimitive(PrimitiveTypeKind.BOOLEAN)
                || TypeTestUtil.isA("java.util.concurrent.atomic.AtomicBoolean", node)
                || TypeTestUtil.isA("java.util.function.Predicate", node);
    }

    private void checkBooleanMethods(ASTMethodDeclaration node, Object data, String nameOfMethod) {
        ASTType t = node.getResultTypeNode();
        if (!t.isVoid()) {
            for (String prefix : getProperty(BOOLEAN_METHOD_PREFIXES_PROPERTY)) {
                if (startsWithCamelCaseWord(nameOfMethod, prefix) && !isBooleanType(t)) {
                    addViolationWithMessage(data, node, "Linguistics Antipattern - The method ''{0}'' indicates linguistically it returns a boolean, but it returns ''{1}''",
                            new Object[] {nameOfMethod, PrettyPrintingUtil.prettyPrintType(t) });
                }
            }
        }
    }

    private void checkField(ASTType typeNode, ASTVariableDeclarator node, Object data) {
        for (String prefix : getProperty(BOOLEAN_FIELD_PREFIXES_PROPERTY)) {
            if (startsWithCamelCaseWord(node.getName(), prefix) && !isBooleanType(typeNode)) {
                addViolationWithMessage(data, node, "Linguistics Antipattern - The field ''{0}'' indicates linguistically it is a boolean, but it is ''{1}''",
                        new Object[] { node.getName(), PrettyPrintingUtil.prettyPrintType(typeNode) });
            }
        }
    }

    private void checkVariable(ASTType typeNode, ASTVariableDeclarator node, Object data) {
        for (String prefix : getProperty(BOOLEAN_FIELD_PREFIXES_PROPERTY)) {
            if (startsWithCamelCaseWord(node.getName(), prefix) && !isBooleanType(typeNode)) {
                addViolationWithMessage(data, node, "Linguistics Antipattern - The variable ''{0}'' indicates linguistically it is a boolean, but it is ''{1}''",
                        new Object[] { node.getName(), PrettyPrintingUtil.prettyPrintType(typeNode) });
            }
        }
    }

    @Override
    public Object visit(ASTFieldDeclaration node, Object data) {
        ASTType type = node.getTypeNode();
        if (type != null && getProperty(CHECK_FIELDS)) {
            for (ASTVariableDeclarator field : node.children(ASTVariableDeclarator.class)) {
                checkField(type, field, data);
            }
        }
        return data;
    }

    @Override
    public Object visit(ASTLocalVariableDeclaration node, Object data) {
        ASTType type = node.getTypeNode();
        if (type != null && getProperty(CHECK_VARIABLES)) {
            for (ASTVariableDeclarator variable : node.children(ASTVariableDeclarator.class)) {
                checkVariable(type, variable, data);
            }
        }
        return data;
    }

}
