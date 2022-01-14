/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import static net.sourceforge.pmd.properties.PropertyFactory.booleanProperty;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.rule.internal.TestFrameworksUtil;
import net.sourceforge.pmd.lang.java.types.JMethodSig;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.properties.PropertyDescriptor;


/**
 * A method/constructor shouldn't explicitly throw java.lang.Exception, since it
 * is unclear which exceptions that can be thrown from the methods. It might be
 * difficult to document and understand such vague interfaces. Use either a class
 * derived from RuntimeException or a checked exception.
 *
 * <p>This rule uses PMD's type resolution facilities, and can detect
 * if the class implements or extends TestCase class
 *
 * @author <a href="mailto:trondandersen@c2i.net">Trond Andersen</a>
 * @version 1.0
 * @since 1.2
 */

public class SignatureDeclareThrowsExceptionRule extends AbstractJavaRule {

    private static final PropertyDescriptor<Boolean> IGNORE_JUNIT_COMPLETELY_DESCRIPTOR =
            booleanProperty("IgnoreJUnitCompletely").defaultValue(false)
            .desc("Allow all methods in a JUnit3 TestCase to throw Exceptions").build();

    // Set to true when the class is determined to be a JUnit testcase
    private boolean junit3TestClass = false;

    public SignatureDeclareThrowsExceptionRule() {
        definePropertyDescriptor(IGNORE_JUNIT_COMPLETELY_DESCRIPTOR);
    }

    @Override
    public void start(RuleContext ctx) {
        super.start(ctx);
        junit3TestClass = false;
    }

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (junit3TestClass) {
            return super.visit(node, data);
        }

        if (TestFrameworksUtil.isJUnit3Class(node)) {
            junit3TestClass = true;
            return super.visit(node, data);
        }

        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTMethodDeclaration methodDeclaration, Object o) {
        if (junit3TestClass && getProperty(IGNORE_JUNIT_COMPLETELY_DESCRIPTOR)
                || TestFrameworksUtil.isTestMethod(methodDeclaration)
                || TestFrameworksUtil.isTestConfigurationMethod(methodDeclaration)
                // Ignore overridden methods, the issue should be marked on the method definition
                || methodDeclaration.isAnnotationPresent(Override.class)) {
            return super.visit(methodDeclaration, o);
        }

        if (checkExceptions(methodDeclaration.getGenericSignature())) {
            addViolation(o, methodDeclaration);
        }

        return super.visit(methodDeclaration, o);
    }

    @Override
    public Object visit(ASTConstructorDeclaration constructorDeclaration, Object o) {
        if (junit3TestClass && getProperty(IGNORE_JUNIT_COMPLETELY_DESCRIPTOR)) {
            return super.visit(constructorDeclaration, o);
        }

        if (checkExceptions(constructorDeclaration.getGenericSignature())) {
            addViolation(o, constructorDeclaration);
        }
        return super.visit(constructorDeclaration, o);
    }

    /**
     * Checks all exceptions for possible violation on the exception
     * declaration.
     *
     * @return true if "java.lang.Exception" has been declared
     */
    private boolean checkExceptions(JMethodSig signature) {
        for (JTypeMirror exception : signature.getThrownExceptions()) {
            if (TypeTestUtil.isA(Exception.class, exception)) {
                return true;
            }
        }
        return false;
    }
}
