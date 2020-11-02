/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import static net.sourceforge.pmd.util.CollectionUtil.setOf;

import java.util.Set;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJUnitRule;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.lang.rule.RuleTargetSelector;

public class JUnitTestsShouldIncludeAssertRule extends AbstractJUnitRule {


    private static final Set<String> MOCKITO = setOf("org.mockito.Mockito");
    private static final Set<String> ASSERT_CONTAINERS = setOf("org.junit.Assert",
                                                       "org.junit.jupiter.api.Assertions",
                                                       "org.hamcrest.MatcherAssert",
                                                       "junit.framework.TestCase");

    @Override
    protected @NonNull RuleTargetSelector buildTargetSelector() {
        return RuleTargetSelector.forTypes(ASTMethodDeclaration.class);
    }

    @Override
    public Object visit(ASTMethodDeclaration method, Object data) {
        ASTBlock body = method.getBody();
        if (body != null
            && isJUnitMethod(method)
            && !isExpectAnnotated(method)
            && body.descendants(ASTMethodCall.class).none(this::isAssertCall)) {
            addViolation(data, method);
        }
        return data;
    }

    /**
     * Tells if the node contains a Test annotation with an expected exception.
     */
    private boolean isExpectAnnotated(ASTMethodDeclaration method) {
        return method.getDeclaredAnnotations()
                     .filter(it -> TypeTestUtil.isA(JUNIT4_CLASS_NAME, it))
                     .flatMap(ASTAnnotation::getMembers)
                     .any(it -> "expected".equals(it.getName()));

    }

    private boolean isAssertCall(ASTMethodCall call) {
        String name = call.getMethodName();
        return name.equals("expect") && TypeTestUtil.isA("org.junit.rules.ExpectedException", call.getQualifier())
            || name.equals("assertAll") && TypeTestUtil.isA("org.assertj.core.api.SoftAssertions", call.getQualifier())
            || name.equals("verify") && isCallOnType(call, MOCKITO)
            || (name.startsWith("assert") || name.equals("fail")) && isCallOnType(call, ASSERT_CONTAINERS);
    }

    private boolean isCallOnType(ASTMethodCall call, Set<String> qualifierTypes) {
        JTypeMirror declaring = call.getMethodType().getDeclaringType();
        JTypeDeclSymbol sym = declaring.getSymbol();
        String binaryName = !(sym instanceof JClassSymbol) ? null : ((JClassSymbol) sym).getBinaryName();
        return qualifierTypes.contains(binaryName);
    }
}
