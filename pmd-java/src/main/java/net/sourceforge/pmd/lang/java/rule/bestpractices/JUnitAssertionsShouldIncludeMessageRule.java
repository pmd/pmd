/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.java.ast.ASTAnnotation;
import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTArguments;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTExtendsList;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class JUnitAssertionsShouldIncludeMessageRule extends AbstractJavaRule {

    private static final String JUNIT3_PACKAGE = "junit.framework";
    private static final String JUNIT4_PACKAGE = "org.junit";

    private List<AssertionCall> checks = new ArrayList<>();
    private boolean isJUnit;

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
    public void start(RuleContext ctx) {
        super.start(ctx);
        isJUnit = false;
    }

    @Override
    public Object visit(ASTImportDeclaration node, Object data) {
        isJUnit |= node.getImportedName().startsWith(JUNIT4_PACKAGE);
        isJUnit |= node.getImportedName().startsWith(JUNIT3_PACKAGE);
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTExtendsList node, Object data) {
        for (ASTClassOrInterfaceType type : node) {
            isJUnit |= type.getImage().startsWith(JUNIT3_PACKAGE);
        }
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTAnnotation node, Object data) {
        isJUnit |= node.getAnnotationName().startsWith(JUNIT4_PACKAGE);
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTArguments node, Object data) {
        if (isJUnit) {
            for (AssertionCall call : checks) {
                JavaNode foundAssertCall = call.check(data, node);
                if (foundAssertCall != null) {
                    addViolation(data, foundAssertCall);
                }
            }
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

    private static class AssertionCall {
        private final int argumentsCount;
        private final String assertionName;

        AssertionCall(String assertionName, int argumentsCount) {
            this.argumentsCount = argumentsCount;
            this.assertionName = assertionName;
        }

        public JavaNode check(Object ctx, ASTArguments node) {
            if (node.size() == argumentsCount
                    && node.getNthParent(2) instanceof ASTPrimaryExpression) {
                ASTPrimaryPrefix primaryPrefix = node.getNthParent(2).getFirstChildOfType(ASTPrimaryPrefix.class);

                if (isException(node)) {
                    return null;
                }

                if (primaryPrefix != null) {
                    ASTName name = primaryPrefix.getFirstChildOfType(ASTName.class);

                    if (name != null
                            && (name.hasImageEqualTo(this.assertionName)
                                    || name.getImage().endsWith("." + this.assertionName))) {
                        return name;
                    }
                }
            }
            return null;
        }

        @Override
        public String toString() {
            return "Assertion[" + assertionName + ",args=" + argumentsCount + "]";
        }

        protected boolean isException(ASTArguments node) {
            return false;
        }
    }
}
