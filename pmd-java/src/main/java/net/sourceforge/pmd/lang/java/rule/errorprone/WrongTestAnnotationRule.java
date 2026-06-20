/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import static net.sourceforge.pmd.lang.java.rule.internal.TestFrameworksUtil.isJunit4ConfigAnnotation;
import static net.sourceforge.pmd.lang.java.rule.internal.TestFrameworksUtil.isJunit4TestAnnotation;
import static net.sourceforge.pmd.lang.java.rule.internal.TestFrameworksUtil.isJunit5ConfigAnnotation;
import static net.sourceforge.pmd.lang.java.rule.internal.TestFrameworksUtil.isJunit5TestAnnotation;
import static net.sourceforge.pmd.lang.java.rule.internal.TestFrameworksUtil.isTestNGConfigAnnotation;
import static net.sourceforge.pmd.lang.java.rule.internal.TestFrameworksUtil.isTestNGTestAnnotation;
import static net.sourceforge.pmd.properties.PropertyFactory.conventionalEnumListProperty;

import java.util.List;
import java.util.stream.Collectors;

import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.reporting.RuleContext;

/**
 * @since 7.26.0
 */
public class WrongTestAnnotationRule extends AbstractJavaRulechainRule {

    private static final PropertyDescriptor<List<TestFrameworks>> FRAMEWORKS_DESCRIPTOR
            = conventionalEnumListProperty("testFrameworks", TestFrameworks.class)
                    .desc("List of test frameworks your codebase uses.")
                    .defaultValues(TestFrameworks.J_UNIT_JUPITER)
                    .build();


    public WrongTestAnnotationRule() {
        super(ASTAnnotation.class);

        definePropertyDescriptor(FRAMEWORKS_DESCRIPTOR);
    }

    @Override
    public Object visit(ASTAnnotation node, Object data) {
        RuleContext ctx = (RuleContext) data;

        if (!getProperty(FRAMEWORKS_DESCRIPTOR).contains(TestFrameworks.J_UNIT_JUPITER)
                && (isJunit5TestAnnotation(node) || isJunit5ConfigAnnotation(node))
        ) {
            addViolation(ctx, node, TestFrameworks.J_UNIT_JUPITER);
        }

        if (!getProperty(FRAMEWORKS_DESCRIPTOR).contains(TestFrameworks.J_UNIT4)
                && (isJunit4TestAnnotation(node) || isJunit4ConfigAnnotation(node))
        ) {
            addViolation(ctx, node, TestFrameworks.J_UNIT4);
        }

        if (!getProperty(FRAMEWORKS_DESCRIPTOR).contains(TestFrameworks.TEST_N_G)
                && (isTestNGTestAnnotation(node) || isTestNGConfigAnnotation(node))
        ) {
            addViolation(ctx, node, TestFrameworks.TEST_N_G);
        }

        return null;
    }

    private void addViolation(RuleContext ctx, ASTAnnotation node, TestFrameworks testFramework) {
        ctx.addViolation(
                node,
                node.getTypeMirror().getSymbol().getBinaryName(),
                readableName(testFramework),
                getProperty(FRAMEWORKS_DESCRIPTOR).stream().map(this::readableName).collect(Collectors.joining(", "))
        );
    }

    private String readableName(TestFrameworks testFramework) {
        switch (testFramework) {
        case J_UNIT_JUPITER: return "JUnit Jupiter";
        case J_UNIT4: return "JUnit 4";
        case TEST_N_G: return "TestNG";
        }

        throw new IllegalArgumentException("Unknown test framework: " + testFramework);
    }

    private enum TestFrameworks {
        J_UNIT_JUPITER,
        J_UNIT4,
        TEST_N_G
    }
}
