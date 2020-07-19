/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.lang.ast.NodeStream;

/**
 * A fallthrough switch branch. This contains exactly one label, and zero
 * or more statements. Fallthrough must be handled by looking at the siblings.
 * For example, in the following, the branch for {@code case 1:} has no statements,
 * while the branch for {@code case 2:} has two.
 *
 * <pre>{@code
 *
 * switch (foo) {
 *  case 1:
 *  case 2:
 *      do1Or2();
 *      break;
 *  default:
 *      doDefault();
 *      break;
 * }
 *
 * }</pre>
 *
 *
 * <pre class="grammar">
 *
 * SwitchFallthroughBranch ::= {@link ASTSwitchLabel SwitchLabel} ":" {@link ASTStatement Statement}*
 *
 * </pre>
 */
public final class ASTSwitchFallthroughBranch extends AbstractJavaNode
    implements ASTSwitchBranch {

    ASTSwitchFallthroughBranch(int id) {
        super(id);
    }

    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    /**
     * Returns the list of statements dominated by the labels. This list is possibly empty.
     */
    public NodeStream<ASTStatement> getStatements() {
        return children(ASTStatement.class);
    }

}
