/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.JModifier;
import net.sourceforge.pmd.lang.java.rule.internal.TestFrameworksUtil;
import net.sourceforge.pmd.properties.PropertyBuilder.RegexPropertyBuilder;
import net.sourceforge.pmd.properties.PropertyDescriptor;


public class MethodNamingConventionsRule extends AbstractNamingConventionRule<ASTMethodDeclaration> {

    private final Map<String, String> descriptorToDisplayName = new HashMap<>();

    private final PropertyDescriptor<Pattern> instanceRegex = defaultProp("", "instance").build();
    private final PropertyDescriptor<Pattern> staticRegex = defaultProp("static").build();
    private final PropertyDescriptor<Pattern> nativeRegex = defaultProp("native").build();
    private final PropertyDescriptor<Pattern> junit3Regex = defaultProp("JUnit 3 test").defaultValue("test[A-Z0-9][a-zA-Z0-9]*").build();
    private final PropertyDescriptor<Pattern> junit4Regex = defaultProp("JUnit 4 test").build();
    private final PropertyDescriptor<Pattern> junit5Regex = defaultProp("JUnit 5 test").build();


    public MethodNamingConventionsRule() {
        super(ASTMethodDeclaration.class);
        definePropertyDescriptor(instanceRegex);
        definePropertyDescriptor(staticRegex);
        definePropertyDescriptor(nativeRegex);
        definePropertyDescriptor(junit3Regex);
        definePropertyDescriptor(junit4Regex);
        definePropertyDescriptor(junit5Regex);
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {

        if (node.isOverridden()) {
            return super.visit(node, data);
        }

        if (node.hasModifiers(JModifier.NATIVE)) {
            checkMatches(node, nativeRegex, data);
        } else if (node.isStatic()) {
            checkMatches(node, staticRegex, data);
        } else if (TestFrameworksUtil.isJUnit5Method(node)) {
            checkMatches(node, junit5Regex, data);
        } else if (TestFrameworksUtil.isJUnit4Method(node)) {
            checkMatches(node, junit4Regex, data);
        } else if (TestFrameworksUtil.isJUnit3Method(node)) {
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

        descriptorToDisplayName.put(prop.getName(), display);

        return prop;
    }


    @Override
    String kindDisplayName(ASTMethodDeclaration node, PropertyDescriptor<Pattern> descriptor) {
        return descriptorToDisplayName.get(descriptor.name());
    }
}
