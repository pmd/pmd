/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import static net.sourceforge.pmd.properties.internal.PropertyParsingUtil.DEPRECATED_RULE_PROPERTY_MARKER;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.JModifier;
import net.sourceforge.pmd.lang.java.rule.internal.TestFrameworksUtil;
import net.sourceforge.pmd.properties.PropertyBuilder.RegexPropertyBuilder;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.reporting.RuleContext;


public class MethodNamingConventionsRule extends AbstractNamingConventionRule<ASTMethodDeclaration> {

    private final Map<String, String> descriptorToDisplayName = new HashMap<>();

    private final PropertyDescriptor<Pattern> instanceRegex = defaultProp("", "instance").build();
    private final PropertyDescriptor<Pattern> staticRegex = defaultProp("static").build();
    private final PropertyDescriptor<Pattern> nativeRegex = defaultProp("native").build();
    private final PropertyDescriptor<Pattern> junit3Regex = defaultProp("JUnit 3 test").defaultValue("test[A-Z0-9][a-zA-Z0-9]*").build();
    private final PropertyDescriptor<Pattern> junit4Regex = defaultProp("JUnit 4 test").build();
    private final PropertyDescriptor<Pattern> junit5Regex = defaultProp("JUnit 5 test").desc(DEPRECATED_RULE_PROPERTY_MARKER + "Use junitJupiterTestPattern instead").build();
    private final PropertyDescriptor<Pattern> junitJupiterRegex = defaultProp("JUnit Jupiter test").build();


    public MethodNamingConventionsRule() {
        super(ASTMethodDeclaration.class);
        definePropertyDescriptor(instanceRegex);
        definePropertyDescriptor(staticRegex);
        definePropertyDescriptor(nativeRegex);
        definePropertyDescriptor(junit3Regex);
        definePropertyDescriptor(junit4Regex);
        definePropertyDescriptor(junit5Regex);
        definePropertyDescriptor(junitJupiterRegex);
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        RuleContext ctx = (RuleContext) data;

        if (node.isOverride()) {
            return null;
        }

        if (node.hasModifiers(JModifier.NATIVE)) {
            checkMatches(node, nativeRegex, ctx);
        } else if (node.isStatic()) {
            checkMatches(node, staticRegex, ctx);
        } else if (TestFrameworksUtil.isJUnit5Method(node)) {
            PropertyDescriptor<Pattern> junitJupiterRegexToUse =
                    (isPropertyOverridden(junitJupiterRegex) || !isPropertyOverridden(junit5Regex))
                            ? junitJupiterRegex
                            : junit5Regex;
            checkMatches(node, junitJupiterRegexToUse, ctx);
        } else if (TestFrameworksUtil.isJUnit4Method(node)) {
            checkMatches(node, junit4Regex, ctx);
        } else if (TestFrameworksUtil.isJUnit3Method(node)) {
            checkMatches(node, junit3Regex, ctx);
        } else {
            checkMatches(node, instanceRegex, ctx);
        }

        return null;
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
