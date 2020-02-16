/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTArguments;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.rule.AbstractJUnitRule;

public class JUnitAssertionsShouldIncludeMessageRule extends AbstractJUnitRule {

    private class AssertionCall {
        private final int argumentsCount;
        private final String assertionName;

        AssertionCall(String assertionName, int argumentsCount) {
            this.argumentsCount = argumentsCount;
            this.assertionName = assertionName;
        }

        public void check(Object ctx, ASTArguments node) {
            if (node.size() == argumentsCount
                    && node.getNthParent(2) instanceof ASTPrimaryExpression) {
                ASTPrimaryPrefix primaryPrefix = node.getNthParent(2).getFirstChildOfType(ASTPrimaryPrefix.class);

                if (primaryPrefix != null) {
                    ASTName name = primaryPrefix.getFirstChildOfType(ASTName.class);

                    if (name != null && name.hasImageEqualTo(this.assertionName)) {
                        if (isException(node)) {
                            return;
                        }
                        JUnitAssertionsShouldIncludeMessageRule.this.addViolation(ctx, name);
                    }
                }
            }
        }

        protected boolean isException(ASTArguments node) {
            return false;
        }
    }

    private List<AssertionCall> checks = new ArrayList<>();

    public JUnitAssertionsShouldIncludeMessageRule() {
        checks.add(new AssertionCall("assertArrayEquals", 2));
        checks.add(new AssertionCall("assertEquals", 2));
        checks.add(new AssertionCall("assertFalse", 1));
        checks.add(new AssertionCall("assertNotNull", 1));
        checks.add(new AssertionCall("assertNotSame", 2));
        checks.add(new AssertionCall("assertNull", 1));
        checks.add(new AssertionCall("assertSame", 2));
        checks.add(new AssertionCall("assertThat", 2));
        checks.add(new AssertionCall("assertTrue", 1));
        checks.add(new AssertionCall("fail", 0));

        checks.add(new AssertionCall("assertEquals", 3) {
            @Override
            protected boolean isException(ASTArguments node) {
                // consider the top-level expressions of the arguments: Arguments/ArgumentList/Expression
                ASTArgumentList argumentList = node.getFirstChildOfType(ASTArgumentList.class);
                List<ASTExpression> arguments = argumentList.findChildrenOfType(ASTExpression.class);
                boolean isExceptionJunit4 = isStringTypeOrNull(arguments.get(0));
                boolean isExceptionJunit5 = isStringTypeOrNull(arguments.get(2));

                return isExceptionJunit4 || isExceptionJunit5;
            }
        });
    }

    @Override
    public Object visit(ASTArguments node, Object data) {
        for (AssertionCall call : checks) {
            call.check(data, node);
        }
        return super.visit(node, data);
    }

    /**
     * @param node
     *            the node to check
     * @return {@code true} if node's type is String or null, otherwise {@code false}
     */
    private boolean isStringTypeOrNull(ASTExpression node) {
        return node.getType() == String.class || node.getType() == null;
    }
}
