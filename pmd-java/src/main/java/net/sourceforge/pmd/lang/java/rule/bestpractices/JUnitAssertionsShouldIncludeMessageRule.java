/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import java.util.ArrayList;
import java.util.List;

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
            if (node.getArgumentCount() == argumentsCount
                    && node.jjtGetParent().jjtGetParent() instanceof ASTPrimaryExpression) {
                ASTPrimaryExpression primary = (ASTPrimaryExpression) node.jjtGetParent().jjtGetParent();
                if (primary.jjtGetChild(0) instanceof ASTPrimaryPrefix && primary.jjtGetChild(0).jjtGetNumChildren() > 0
                        && primary.jjtGetChild(0).jjtGetChild(0) instanceof ASTName) {
                    ASTName name = (ASTName) primary.jjtGetChild(0).jjtGetChild(0);

                    if (name.hasImageEqualTo(this.assertionName)) {
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
                ASTExpression firstArgument = node.getFirstDescendantOfType(ASTExpression.class);
                return firstArgument.getType() == null || firstArgument.getType() == String.class;
            }
        });
    }

    public Object visit(ASTArguments node, Object data) {
        for (AssertionCall call : checks) {
            call.check(data, node);
        }
        return super.visit(node, data);
    }
}
