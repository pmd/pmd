/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static net.sourceforge.pmd.util.CollectionUtil.setOf;

import java.util.Set;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTStringLiteral;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.reporting.RuleContext;

public class LiteralsFirstInComparisonsRule extends AbstractJavaRulechainRule {

    private static final Set<String> STRING_COMPARISONS =
        setOf("equalsIgnoreCase",
              "compareTo",
              "compareToIgnoreCase",
              "contentEquals");
    private static final String EQUALS = "equals";

    public LiteralsFirstInComparisonsRule() {
        super(ASTMethodCall.class);
    }

    @Override
    public Object visit(ASTMethodCall call, Object data) {
        if (shouldCheckArgs(call)) {
            checkArgs((RuleContext) data, call);
        }
        return data;
    }

    private boolean shouldCheckArgs(ASTMethodCall call) {
        return (EQUALS.equals(call.getMethodName()) && call.getArguments().size() == 1 && isEqualsObjectAndNotAnOverload(call))
                || (STRING_COMPARISONS.contains(call.getMethodName()) && call.getArguments().size() == 1
                && TypeTestUtil.isDeclaredInClass(String.class, call.getMethodType()));
    }


    private boolean isEqualsObjectAndNotAnOverload(ASTMethodCall call) {
        return call.getOverloadSelectionInfo().isFailed() // failed selection is considered probably equals(Object)
                || call.getMethodType().getFormalParameters().equals(listOf(call.getTypeSystem().OBJECT));
    }

    private void checkArgs(RuleContext ctx, ASTMethodCall call) {
        if (!isConstantString(call.getQualifier()) && isConstantString(call.getArguments().get(0))) {
            ctx.addViolation(call);
        }
    }

    private boolean isConstantString(@Nullable ASTExpression node) {
        return node != null
                && (node instanceof ASTStringLiteral || node.getConstValue() instanceof String);
    }
}
