/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules.junit;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTArguments;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.ast.ASTPrimaryPrefix;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class JUnitAssertionsShouldIncludeMessage extends AbstractRule {

    private static class AssertionCall {
        public int args;
        public String name;

        public AssertionCall(int args, String name) {
            this.args = args;
            this.name = name;
        }
    }

    private List checks = new ArrayList();

    public JUnitAssertionsShouldIncludeMessage() {
        checks.add(new AssertionCall(2, "assertEquals"));
        checks.add(new AssertionCall(1, "assertTrue"));
        checks.add(new AssertionCall(1, "assertNull"));
        checks.add(new AssertionCall(2, "assertSame"));
        checks.add(new AssertionCall(1, "assertNotNull"));
        checks.add(new AssertionCall(1, "assertFalse"));
    }

    public Object visit(ASTArguments node, Object data) {
        for (Iterator i = checks.iterator(); i.hasNext();) {
            AssertionCall call = (AssertionCall) i.next();
            check(data, node, call.args, call.name);
        }
        return super.visit(node, data);
    }

    private void check(Object ctx, ASTArguments node, int args, String targetMethodName) {
        if (node.getArgumentCount() == args && node.jjtGetParent().jjtGetParent() instanceof ASTPrimaryExpression) {
            ASTPrimaryExpression primary = (ASTPrimaryExpression) node.jjtGetParent().jjtGetParent();
            if (primary.jjtGetChild(0) instanceof ASTPrimaryPrefix && primary.jjtGetChild(0).jjtGetNumChildren() > 0 && primary.jjtGetChild(0).jjtGetChild(0) instanceof ASTName) {
                ASTName name = (ASTName) primary.jjtGetChild(0).jjtGetChild(0);
                if (name.hasImageEqualTo(targetMethodName)) {
                    addViolation(ctx, name);
                }
            }
        }
    }
}
