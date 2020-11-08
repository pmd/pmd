/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import static net.sourceforge.pmd.util.CollectionUtil.listOf;
import static net.sourceforge.pmd.util.CollectionUtil.setOf;

import java.util.Set;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTStringLiteral;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;

public class LiteralsFirstInComparisonsRule extends AbstractJavaRulechainRule {

    private static final Set<String> STRING_COMPARISONS =
        setOf("equalsIgnoreCase",
              "compareTo",
              "compareToIgnoreCase",
              "contentEquals");

    public LiteralsFirstInComparisonsRule() {
        super(ASTMethodCall.class);
    }

    @Override
    public Object visit(ASTMethodCall call, Object data) {
        if ("equals".equals(call.getMethodName())
            && call.getArguments().size() == 1
            && isEqualsObjectAndNotAnOverload(call)) {
            checkArgs((RuleContext) data, call);
        } else if (STRING_COMPARISONS.contains(call.getMethodName())
            && call.getArguments().size() == 1
            && TypeTestUtil.isDeclaredInClass(String.class, call.getMethodType())) {
            checkArgs((RuleContext) data, call);
        }
        return data;
    }

    private boolean isEqualsObjectAndNotAnOverload(ASTMethodCall call) {
        if (call.getOverloadSelectionInfo().isFailed()) {
            return true; // failed selection is considered probably equals(Object)
        }
        return call.getMethodType().getFormalParameters().equals(listOf(call.getTypeSystem().OBJECT));
    }

    private void checkArgs(RuleContext ctx, ASTMethodCall call) {
        ASTExpression arg = call.getArguments().get(0);
        ASTExpression qualifier = call.getQualifier();
        if (!(qualifier instanceof ASTStringLiteral) && arg instanceof ASTStringLiteral) {
            addViolation(ctx, call);
        }
    }
}
