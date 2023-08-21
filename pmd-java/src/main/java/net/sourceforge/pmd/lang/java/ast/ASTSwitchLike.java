/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Iterator;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;


/**
 * Common supertype for {@linkplain ASTSwitchStatement switch statements}
 * and {@linkplain ASTSwitchExpression switch expressions}. Their grammar
 * is identical, and is described below. The difference is that switch
 * expressions need to be exhaustive.
 *
 * <pre class="grammar">
 *
 * SwitchLike        ::= {@link ASTSwitchExpression SwitchExpression}
 *                     | {@link ASTSwitchStatement SwitchStatement}
 *
 *                   ::= "switch" "(" {@link ASTExpression Expression} ")" SwitchBlock
 *
 * SwitchBlock       ::= SwitchArrowBlock | SwitchNormalBlock
 *
 * SwitchArrowBlock  ::= "{" {@link ASTSwitchArrowBranch SwitchArrowBranch}* "}"
 * SwitchNormalBlock ::= "{" {@linkplain ASTSwitchFallthroughBranch SwitchFallthroughBranch}* "}"
 *
 * </pre>
 */
public interface ASTSwitchLike extends JavaNode, Iterable<ASTSwitchBranch> {

    /**
     * Returns true if this switch has a {@code default} case.
     */
    default boolean hasDefaultCase() {
        return getBranches().any(it -> it.getLabel().isDefault());
    }


    /**
     * Returns a stream of all branches of this switch.
     */
    default NodeStream<ASTSwitchBranch> getBranches() {
        return children(ASTSwitchBranch.class);
    }


    /**
     * Gets the expression tested by this switch.
     * This is the expression between the parentheses.
     */
    default ASTExpression getTestedExpression() {
        return (ASTExpression) getChild(0);
    }


    /**
     * Returns true if this switch statement tests an expression
     * having an enum type and all the constants of this type
     * are covered by a switch case. Returns false if the type of
     * the tested expression could not be resolved.
     */
    default boolean isExhaustiveEnumSwitch() {
        JTypeDeclSymbol symbol = getTestedExpression().getTypeMirror().getSymbol();
        if (symbol instanceof JClassSymbol && ((JClassSymbol) symbol).isEnum()) {
            long numConstants = ((JClassSymbol) symbol).getEnumConstants().size();
            // we assume there's no duplicate labels
            int numLabels = getBranches().sumByInt(it -> it.getLabel().getNumChildren());
            return numLabels == numConstants;
        }
        return false;
    }

    /**
     * Returns true if this switch statement tests an expression
     * having an enum type.
     */
    default boolean isEnumSwitch() {
        JTypeDeclSymbol type = getTestedExpression().getTypeMirror().getSymbol();
        return type instanceof JClassSymbol && ((JClassSymbol) type).isEnum();
    }


    @Override
    default Iterator<ASTSwitchBranch> iterator() {
        return children(ASTSwitchBranch.class).iterator();
    }

    /**
     * Returns true if this a switch which uses fallthrough branches
     * (old school {@code case label: break;}) and not arrow branches.
     * If the switch has no branches, returns false.
     */
    default boolean isFallthroughSwitch() {
        return getBranches().filterIs(ASTSwitchFallthroughBranch.class).nonEmpty();
    }

}
