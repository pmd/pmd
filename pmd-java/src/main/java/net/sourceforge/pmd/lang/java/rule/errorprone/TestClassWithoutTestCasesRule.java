/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import static net.sourceforge.pmd.lang.java.rule.internal.TestFrameworksUtil.isJUnit3Class;
import static net.sourceforge.pmd.lang.java.rule.internal.TestFrameworksUtil.isJUnit5NestedClass;

import java.util.regex.Pattern;

import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.TestFrameworksUtil;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;

public class TestClassWithoutTestCasesRule extends AbstractJavaRulechainRule {

    private static final PropertyDescriptor<Pattern> TEST_CLASS_PATTERN = PropertyFactory.regexProperty("testClassPattern")
            .defaultValue("^(?:.*\\.)?Test[^\\.]*$|^(?:.*\\.)?.*Tests?$|^(?:.*\\.)?.*TestCase$")
            .desc("Test class name pattern to identify test classes by their fully qualified name. "
                    + "An empty pattern disables test class detection by name. Since PMD 6.51.0.")
            .build();

    public TestClassWithoutTestCasesRule() {
        super(ASTClassOrInterfaceDeclaration.class);
        definePropertyDescriptor(TEST_CLASS_PATTERN);
    }

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (isJUnit3Class(node) || isJUnit5NestedClass(node) || isTestClassByPattern(node)) {
            boolean hasTests =
                node.getDeclarations(ASTMethodDeclaration.class)
                    .any(TestFrameworksUtil::isTestMethod);
            boolean hasNestedTestClasses = node.getDeclarations(ASTAnyTypeDeclaration.class)
                    .any(TestFrameworksUtil::isJUnit5NestedClass);

            if (!hasTests && !hasNestedTestClasses) {
                asCtx(data).addViolation(node, node.getSimpleName());
            }
        }
        return null;
    }

    private boolean isTestClassByPattern(ASTClassOrInterfaceDeclaration node) {
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
