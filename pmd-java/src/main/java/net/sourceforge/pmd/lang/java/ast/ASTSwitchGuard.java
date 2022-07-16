/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.Experimental;

/**
 * A guard for refining a switch case in {@link ASTSwitchLabel}s.
 *
 * <pre class="grammar">
 *
 * SwitchLabel := "case" {@linkplain ASTPattern Pattern} SwitchGuard?
 * SwitchGuard ::= "when" {@linkplain ASTExpression Expression}
 *
 * </pre>
 *
 * @see <a href="https://openjdk.org/jeps/427">JEP 427: Pattern Matching for switch (Third Preview)</a>
*/
@Experimental
public final class ASTSwitchGuard extends AbstractJavaNode {

    ASTSwitchGuard(int id) {
        super(id);
    }

    ASTSwitchGuard(JavaParser p, int id) {
        super(p, id);
    }


    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }

    public ASTExpression getGuard() {
        return (ASTExpression) getChild(0);
    }
}
