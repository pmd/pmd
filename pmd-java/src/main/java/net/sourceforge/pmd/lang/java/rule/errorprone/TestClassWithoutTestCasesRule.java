/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import java.util.List;
import java.util.regex.Pattern;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBody;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceBodyDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTPackageDeclaration;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJUnitRule;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;

public class TestClassWithoutTestCasesRule extends AbstractJavaRule {

    private static final PropertyDescriptor<Pattern> TEST_CLASS_PATTERN = PropertyFactory.regexProperty("testClassPattern")
            .defaultValue("^(?:.*\\.)?Test[^\\.]*$|^(?:.*\\.)?.*Tests?$|^(?:.*\\.)?.*TestCase$")
            .desc("Test class name pattern to identify test classes by their fully qualified name. "
                    + "An empty pattern disables test class detection by name. Since PMD 6.51.0.")
            .build();

    public TestClassWithoutTestCasesRule() {
        addRuleChainVisit(ASTClassOrInterfaceBody.class);
        definePropertyDescriptor(TEST_CLASS_PATTERN);
    }

    @Override
    public Object visit(ASTClassOrInterfaceBody node, Object data) {
        if (AbstractJUnitRule.isTestClass(node) || AbstractJUnitRule.isJUnit5NestedClass(node) || isTestClassByPattern(node)) {
            List<ASTClassOrInterfaceBodyDeclaration> declarations = node.findChildrenOfType(ASTClassOrInterfaceBodyDeclaration.class);
            int testMethods = 0;
            int nestedTestClasses = 0;
            for (ASTClassOrInterfaceBodyDeclaration decl : declarations) {
                if (isTestMethod(decl)) {
                    testMethods++;
                } else if (isNestedClass(decl)) {
                    nestedTestClasses++;
                }
            }
            if (testMethods == 0 && nestedTestClasses == 0) {
                addViolation(data, node, getSimpleClassName(node));
            }
        }
        return data;
    }

    private String getSimpleClassName(ASTClassOrInterfaceBody node) {
        JavaNode parent = node.getParent();
        if (parent instanceof ASTClassOrInterfaceDeclaration) {
            return ((ASTClassOrInterfaceDeclaration) parent).getSimpleName();
        }
        return "<anon>";
    }

    private boolean isTestClassByPattern(ASTClassOrInterfaceBody node) {
        Pattern testClassPattern = getProperty(TEST_CLASS_PATTERN);
        if (testClassPattern.pattern().isEmpty()) {
            // detection by pattern is disabled
            return false;
        }

        ASTClassOrInterfaceDeclaration classDecl = null;
        if (node.getParent() instanceof ASTClassOrInterfaceDeclaration) {
            classDecl = (ASTClassOrInterfaceDeclaration) node.getParent();
        }

        // classDecl can be null in case of anonymous classes or enum constants
        if (classDecl == null || classDecl.isAbstract() || classDecl.isInterface()) {
            return false;
        }

        StringBuilder fullyQualifiedName = new StringBuilder();
        ASTPackageDeclaration packageDeclaration = classDecl.getRoot().getPackageDeclaration();
        if (packageDeclaration != null) {
            fullyQualifiedName.append(packageDeclaration.getName()).append('.');
        }
        List<ASTClassOrInterfaceDeclaration> parentClasses = classDecl.getParentsOfType(ASTClassOrInterfaceDeclaration.class);
        for (int i = parentClasses.size() - 1; i >= 0; i--) {
            fullyQualifiedName.append(parentClasses.get(i).getSimpleName()).append('.');
        }
        fullyQualifiedName.append(classDecl.getSimpleName());
        return testClassPattern.matcher(fullyQualifiedName).find();
    }

    private boolean isTestMethod(ASTClassOrInterfaceBodyDeclaration decl) {
        JavaNode node = decl.getDeclarationNode();
        if (node instanceof ASTMethodDeclaration) {
            return AbstractJUnitRule.isTestMethod((ASTMethodDeclaration) node);
        }
        return false;
    }

    private boolean isNestedClass(ASTClassOrInterfaceBodyDeclaration decl) {
        JavaNode node = decl.getParent();
        if (node instanceof ASTClassOrInterfaceBody) {
            return AbstractJUnitRule.isJUnit5NestedClass((ASTClassOrInterfaceBody) node);
        }
        return false;
    }
}
