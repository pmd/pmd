/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import static net.sourceforge.pmd.properties.PropertyFactory.booleanProperty;

import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodOrConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTThrowsList;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.TestFrameworksUtil;
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

public class SignatureDeclareThrowsExceptionRule extends AbstractJavaRulechainRule {

    private static final PropertyDescriptor<Boolean> IGNORE_JUNIT_COMPLETELY_DESCRIPTOR =
        booleanProperty("IgnoreJUnitCompletely").defaultValue(false)
                                                .desc("Allow all methods in a JUnit3 TestCase to throw Exceptions").build();

    public SignatureDeclareThrowsExceptionRule() {
        super(ASTThrowsList.class);
        definePropertyDescriptor(IGNORE_JUNIT_COMPLETELY_DESCRIPTOR);
    }

    @Override
    public Object visit(ASTThrowsList throwsList, Object o) {
        if (!isIgnored(throwsList.getOwner())
            && throwsList.toStream().any(it -> TypeTestUtil.isExactlyA(Exception.class, it))) {
            addViolation(o, throwsList);
        }
        return null;
    }

    private boolean isIgnored(ASTMethodOrConstructorDeclaration owner) {
        if (getProperty(IGNORE_JUNIT_COMPLETELY_DESCRIPTOR)
            && TestFrameworksUtil.isJUnit3Class(owner.getEnclosingType())) {
            return true;
        } else if (owner instanceof ASTMethodDeclaration) {
            ASTMethodDeclaration m = (ASTMethodDeclaration) owner;
            return TestFrameworksUtil.isTestMethod(m)
                || TestFrameworksUtil.isTestConfigurationMethod(m)
                // Ignore overridden methods, the issue should be marked on the method definition
                || m.isOverridden();
        }
        return false;
    }
}
