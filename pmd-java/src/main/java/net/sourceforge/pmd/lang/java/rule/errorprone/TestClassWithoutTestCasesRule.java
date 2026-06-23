/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import static net.sourceforge.pmd.lang.java.rule.internal.TestFrameworksUtil.hasJUnit3Tests;
import static net.sourceforge.pmd.lang.java.rule.internal.TestFrameworksUtil.hasJUnit4Tests;
import static net.sourceforge.pmd.lang.java.rule.internal.TestFrameworksUtil.hasJUnit5Tests;
import static net.sourceforge.pmd.lang.java.rule.internal.TestFrameworksUtil.hasTestNGTests;
import static net.sourceforge.pmd.lang.java.rule.internal.TestFrameworksUtil.isJUnit3Class;
import static net.sourceforge.pmd.lang.java.rule.internal.TestFrameworksUtil.isJUnit5NestedClass;

import java.util.regex.Pattern;

import net.sourceforge.pmd.lang.java.ast.ASTClassDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;
import net.sourceforge.pmd.reporting.RuleContext;

public class TestClassWithoutTestCasesRule extends AbstractJavaRulechainRule {

    private static final PropertyDescriptor<Pattern> TEST_CLASS_PATTERN = PropertyFactory.regexProperty("testClassPattern")
            .defaultValue("^(?:.*\\.)?Test[^\\.]*$|^(?:.*\\.)?.*Tests?$|^(?:.*\\.)?.*TestCase$")
            .desc("Test class name pattern to identify test classes by their fully qualified name. "
                    + "An empty pattern disables test class detection by name. Since PMD 6.51.0.")
            .build();

    public TestClassWithoutTestCasesRule() {
        super(ASTClassDeclaration.class);
        definePropertyDescriptor(TEST_CLASS_PATTERN);
    }

    @Override
    public Object visit(ASTClassDeclaration node, Object data) {
        RuleContext ctx = (RuleContext) data;

        if (isJUnit3Class(node)) {
            if (!hasJUnit3Tests(node)) {
                ctx.addViolation(node, node.getSimpleName());
            }
        } else if (isJUnit5NestedClass(node)) {
            if (!hasJUnit5Tests(node)) {
                ctx.addViolation(node, node.getSimpleName());
            }
        } else if (isTestClassByPattern(node)) {
            if (!(hasJUnit4Tests(node) || hasJUnit5Tests(node) || hasTestNGTests(node))) {
                ctx.addViolation(node, node.getSimpleName());
            }
        }

        return null;
    }

    private boolean isTestClassByPattern(ASTClassDeclaration node) {
        Pattern testClassPattern = getProperty(TEST_CLASS_PATTERN);
        if (testClassPattern.pattern().isEmpty()) {
            // detection by pattern is disabled
            return false;
        }

        if (node.isAbstract() || node.isInterface()) {
            return false;
        }

        String fullName = node.getCanonicalName();
        return fullName != null && testClassPattern.matcher(fullName).find();
    }
}
