/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import static net.sourceforge.pmd.properties.PropertyFactory.stringListProperty;

import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFinallyClause;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTTryStatement;
import net.sourceforge.pmd.lang.java.ast.ASTTypeExpression;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.JavaRuleUtil;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;
import net.sourceforge.pmd.properties.PropertyDescriptor;

public final class UseTryWithResourcesRule extends AbstractJavaRulechainRule {

    private static final PropertyDescriptor<List<String>> CLOSE_METHODS =
            stringListProperty("closeMethods")
                    .desc("Method names in finally block, which trigger this rule")
                    .defaultValues("close", "closeQuietly")
                    .delim(',')
                    .build();

    public UseTryWithResourcesRule() {
        super(ASTTryStatement.class);
        definePropertyDescriptor(CLOSE_METHODS);
    }

    @Override
    public Object visit(ASTTryStatement node, Object data) {
        boolean isJava9OrLater = node.getAstInfo().getLanguageVersion().compareToVersion("9") >= 0;

        ASTFinallyClause finallyClause = node.getFinallyClause();
        if (finallyClause != null) {
            List<ASTMethodCall> methods = finallyClause.descendants(ASTMethodCall.class)
                .filter(m -> getProperty(CLOSE_METHODS).contains(m.getMethodName()))
                .toList();
            for (ASTMethodCall method : methods) {
                ASTExpression closeTarget = method.getQualifier();
                if (!(closeTarget instanceof ASTTypeExpression) // ignore static method calls
                        && TypeTestUtil.isA(AutoCloseable.class, closeTarget)
                        && (isJava9OrLater || JavaRuleUtil.isReferenceToLocal(closeTarget))
                        || hasAutoClosableArguments(method)) {
                    addViolation(data, node);
                    break; // only report the first closeable
                }
            }
        }
        return data;
    }

    private boolean hasAutoClosableArguments(ASTMethodCall method) {
        return method.getArguments().children()
                .filter(e -> TypeTestUtil.isA(AutoCloseable.class, e))
                .nonEmpty();
    }
}
