/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTClassLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTTypeDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.TestFrameworksUtil;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;

public class UnitTestShouldIncludeAssertRule extends AbstractJavaRulechainRule {

    private static final PropertyDescriptor<Set<String>> EXTRA_ASSERT_METHOD_NAMES =
            PropertyFactory.stringProperty("extraAssertMethodNames")
                           .desc("Extra valid assertion methods names")
                           .map(Collectors.toSet())
                           .emptyDefaultValue()
                           .build();

    public UnitTestShouldIncludeAssertRule() {
        super(ASTMethodDeclaration.class);
        definePropertyDescriptor(EXTRA_ASSERT_METHOD_NAMES);
    }

    @Override
    public Object visit(ASTMethodDeclaration method, Object data) {
        boolean usesSoftAssertExtension = usesSoftAssertExtension(method.getEnclosingType());
        Set<String> extraAsserts = getProperty(EXTRA_ASSERT_METHOD_NAMES);
        Predicate<ASTMethodCall> isAssertCall = usesSoftAssertExtension
                ? TestFrameworksUtil::isSoftAssert
                : TestFrameworksUtil::isProbableAssertCall;

        ASTBlock body = method.getBody();
        if (body != null
            && TestFrameworksUtil.isTestMethod(method)
            && !TestFrameworksUtil.isExpectAnnotated(method)
            && body.descendants(ASTMethodCall.class)
                .none(isAssertCall
                        .or(call -> extraAsserts.contains(call.getMethodName())))) {
            asCtx(data).addViolation(method);
        }
        return data;
    }

    private boolean usesSoftAssertExtension(ASTTypeDeclaration typeDeclaration) {
        ASTAnnotation extendWith = typeDeclaration.getAnnotation("org.junit.jupiter.api.extension.ExtendWith");
        if (extendWith == null) {
            return false;
        }
        return extendWith.getFlatValue("value")
                    .filterIs(ASTClassLiteral.class)
                    .map(ASTClassLiteral::getTypeNode)
                    .any(c -> TypeTestUtil.isA("org.assertj.core.api.junit.jupiter.SoftAssertionsExtension", c));
    }
}
