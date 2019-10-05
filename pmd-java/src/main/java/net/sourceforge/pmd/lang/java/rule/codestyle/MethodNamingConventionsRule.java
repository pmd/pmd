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
import net.sourceforge.pmd.lang.java.typeresolution.TypeHelper;
import net.sourceforge.pmd.properties.BooleanProperty;
import net.sourceforge.pmd.properties.PropertyBuilder.RegexPropertyBuilder;
import net.sourceforge.pmd.properties.PropertyDescriptor;


public class MethodNamingConventionsRule extends AbstractNamingConventionRule<ASTMethodDeclaration> {

    private static final Map<String, String> DESCRIPTOR_TO_DISPLAY_NAME = new HashMap<>();

    @Deprecated
    private static final BooleanProperty CHECK_NATIVE_METHODS_DESCRIPTOR = new BooleanProperty("checkNativeMethods",
                                                                                               "deprecated! Check native methods", true, 1.0f);


    private final PropertyDescriptor<Pattern> instanceRegex = defaultProp("", "instance").build();
    private final PropertyDescriptor<Pattern> staticRegex = defaultProp("static").build();
    private final PropertyDescriptor<Pattern> nativeRegex = defaultProp("native").build();
    private final PropertyDescriptor<Pattern> junit3Regex = defaultProp("JUnit 3 test").defaultValue("test[A-Z0-9][a-zA-Z0-9]*").build();
    private final PropertyDescriptor<Pattern> junit4Regex = defaultProp("JUnit 4 test").build();


    public MethodNamingConventionsRule() {
        definePropertyDescriptor(CHECK_NATIVE_METHODS_DESCRIPTOR);

        definePropertyDescriptor(instanceRegex);
        definePropertyDescriptor(staticRegex);
        definePropertyDescriptor(nativeRegex);
        definePropertyDescriptor(junit3Regex);
        definePropertyDescriptor(junit4Regex);
    }

    private boolean isJunit4Test(ASTMethodDeclaration node) {
        return node.isAnnotationPresent("org.junit.Test");
    }


    private boolean isJunit3Test(ASTMethodDeclaration node) {
        if (!node.getName().startsWith("test")) {
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
                checkMatches(node, nativeRegex, data);
            } else {
                return super.visit(node, data);
            }
        } else if (node.isStatic()) {
            checkMatches(node, staticRegex, data);
        } else if (isJunit4Test(node)) {
            checkMatches(node, junit4Regex, data);
        } else if (isJunit3Test(node)) {
            checkMatches(node, junit3Regex, data);
        } else {
            checkMatches(node, instanceRegex, data);
        }

        return super.visit(node, data);
    }


    @Override
    String defaultConvention() {
        return CAMEL_CASE;
    }


    @Override
    String nameExtractor(ASTMethodDeclaration node) {
        return node.getName();
    }

    @Override
    RegexPropertyBuilder defaultProp(String name, String displayName) {
        String display = (displayName + " method").trim();
        RegexPropertyBuilder prop = super.defaultProp(name.isEmpty() ? "method" : name, display);

        DESCRIPTOR_TO_DISPLAY_NAME.put(prop.getName(), display);

        return prop;
    }


    @Override
    String kindDisplayName(ASTMethodDeclaration node, PropertyDescriptor<Pattern> descriptor) {
        return DESCRIPTOR_TO_DISPLAY_NAME.get(descriptor.name());
    }
}
