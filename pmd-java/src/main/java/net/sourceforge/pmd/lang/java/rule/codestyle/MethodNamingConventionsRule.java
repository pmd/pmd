/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAllocationExpression;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTEnumConstant;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.typeresolution.TypeHelper;
import net.sourceforge.pmd.properties.BooleanProperty;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.RegexProperty;
import net.sourceforge.pmd.properties.RegexProperty.RegexPBuilder;
import net.sourceforge.pmd.util.StringUtil;


public class MethodNamingConventionsRule extends AbstractJavaRule {

    private static final Map<String, String> DESCRIPTOR_TO_DISPLAY_NAME = new HashMap<>();

    @Deprecated
    private static final BooleanProperty CHECK_NATIVE_METHODS_DESCRIPTOR = new BooleanProperty("checkNativeMethods",
                                                                                               "deprecated! Check native methods", true, 1.0f);

    private static final RegexProperty INSTANCE_REGEX = defaultProp("method").desc("Regex which applies to instance method names").build();
    private static final RegexProperty STATIC_REGEX = defaultProp("static").build();
    private static final RegexProperty NATIVE_REGEX = defaultProp("native").build();
    private static final RegexProperty JUNIT3_REGEX = defaultProp("JUnit 3 test").defaultValue("test[A-Z0-9][a-zA-Z0-9]*").build();
    private static final RegexProperty JUNIT4_REGEX = defaultProp("JUnit 4 test").build();


    public MethodNamingConventionsRule() {
        definePropertyDescriptor(CHECK_NATIVE_METHODS_DESCRIPTOR);

        definePropertyDescriptor(INSTANCE_REGEX);
        definePropertyDescriptor(STATIC_REGEX);
        definePropertyDescriptor(NATIVE_REGEX);
        definePropertyDescriptor(JUNIT3_REGEX);
        definePropertyDescriptor(JUNIT4_REGEX);
    }

    private void checkMatches(ASTMethodDeclaration node, PropertyDescriptor<Pattern> regex, Object data) {
        if (!getProperty(regex).matcher(node.getMethodName()).matches()) {
            addViolation(data, node.getMethodDeclarator(), new Object[]{
                    DESCRIPTOR_TO_DISPLAY_NAME.get(regex.name()) + " method",
                    node.getMethodName(),
                    getProperty(regex).toString(),
            });
        }
    }


    private boolean isJunit4Test(ASTMethodDeclaration node) {
        return node.isAnnotationPresent("org.junit.Test");
    }


    private boolean isJunit3Test(ASTMethodDeclaration node) {
        if (!node.getMethodName().startsWith("test")) {
            return false;
        }

        // Considers anonymous classes, TODO with #905 this will be easier
        Node parent = node.getFirstParentOfAnyType(ASTEnumConstant.class, ASTAllocationExpression.class, ASTAnyTypeDeclaration.class);

        if (!(parent instanceof ASTClassOrInterfaceDeclaration) || ((ASTClassOrInterfaceDeclaration) parent).isInterface()) {
            return false;
        }

        ASTClassOrInterfaceType superClass = ((ASTClassOrInterfaceDeclaration) parent).getSuperClassTypeNode();

        return superClass != null && TypeHelper.isA(superClass, "junit.framework.TestCase");
    }


    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {

        if (node.isAnnotationPresent("java.lang.Override")) {
            return super.visit(node, data);
        }

        if (node.isNative()) {
            if (getProperty(CHECK_NATIVE_METHODS_DESCRIPTOR)) {
                checkMatches(node, NATIVE_REGEX, data);
            } else {
                return super.visit(node, data);
            }
        } else if (node.isStatic()) {
            checkMatches(node, STATIC_REGEX, data);
        } else if (isJunit4Test(node)) {
            checkMatches(node, JUNIT4_REGEX, data);
        } else if (isJunit3Test(node)) {
            checkMatches(node, JUNIT3_REGEX, data);
        } else {
            checkMatches(node, INSTANCE_REGEX, data);
        }

        return super.visit(node, data);
    }


    private static RegexPBuilder defaultProp(String displayName) {
        String propName = StringUtil.toCamelCase(displayName, true) + "Pattern";
        DESCRIPTOR_TO_DISPLAY_NAME.put(propName, displayName);

        return RegexProperty.named(propName)
                            .desc("Regex which applies to " + displayName.trim() + " method names")
                            .defaultValue("[a-z][a-zA-Z0-9]+");

    }
}
