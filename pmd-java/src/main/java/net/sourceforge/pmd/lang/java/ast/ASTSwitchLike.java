/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.Collectors;

import net.sourceforge.pmd.lang.ast.NodeStream;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeDeclSymbol;
import net.sourceforge.pmd.lang.java.types.JTypeMirror;


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
     * Returns true if this switch block tests an expression
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
     * Returns true if this switch block tests an expression
     * having an enum type.
     */
    default boolean isEnumSwitch() {
        JTypeDeclSymbol type = getTestedExpression().getTypeMirror().getSymbol();
        return type instanceof JClassSymbol && ((JClassSymbol) type).isEnum();
    }

    /**
     * Returns true if this switch block tests an expression
     * having a sealed type or an enum type and all the possible
     * constants or types are covered by a switch case.
     * Returns false if the type of the tested expression could not
     * be resolved.
     *
     * @see #isExhaustiveEnumSwitch()
     */
    default boolean isExhaustive() {
        JTypeDeclSymbol symbol = getTestedExpression().getTypeMirror().getSymbol();

        // shortcut1 - if we have any type patterns and there is no default case,
        // then the compiler already ensured that the switch is exhaustive.
        // This assumes, we only analyze valid, compiled source code.
        boolean hasPatterns = getBranches().map(ASTSwitchBranch::getLabel)
                .any(ASTSwitchLabel::isPatternLabel);
        if (hasPatterns && !hasDefaultCase()) {
            return true;
        }

        if (symbol instanceof JClassSymbol) {
            JClassSymbol classSymbol = (JClassSymbol) symbol;

            // shortcut2 - if we are dealing with a sealed type or a boolean (java 23 preview, JEP 455)
            // and there is no default case then the compiler already checked for exhaustiveness
            if (classSymbol.isSealed() || classSymbol.equals(getTypeSystem().BOOLEAN.getSymbol())) {
                if (!hasDefaultCase()) {
                    return true;
                }
            }

            if (classSymbol.isSealed()) {
                Set<JClassSymbol> checkedSubtypes = getBranches()
                        .map(ASTSwitchBranch::getLabel)
                        .children(ASTTypePattern.class)
                        .map(ASTTypePattern::getTypeNode)
                        .toStream()
                        .map(TypeNode::getTypeMirror)
                        .map(JTypeMirror::getSymbol)
                        .filter(s -> s instanceof JClassSymbol)
                        .map(s -> (JClassSymbol) s)
                        .collect(Collectors.toSet());

                Set<JClassSymbol> permittedSubtypes = new HashSet<>(classSymbol.getPermittedSubtypes());
                // for all the switch cases, remove the checked type itself
                permittedSubtypes.removeAll(checkedSubtypes);

                // if there are any remaining types left, they might be covered, if they are sealed
                // (there are no other possible subtypes) and all subtypes are covered
                // Note: This currently only checks one level. If the type hierarchy is deeper, we don't
                // recognize all possible permitted subtypes.
                for (JClassSymbol remainingType : new HashSet<>(permittedSubtypes)) {
                    if (remainingType.isSealed()) {
                        Set<JClassSymbol> subtypes = new HashSet<>(remainingType.getPermittedSubtypes());
                        subtypes.removeAll(checkedSubtypes);
                        if (subtypes.isEmpty()) {
                            permittedSubtypes.remove(remainingType);
                        }
                    }
                }

                return permittedSubtypes.isEmpty();
            }
        }

        return isExhaustiveEnumSwitch();
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

    /**
     * Return true if this switch accepts null. This must be explicitly
     * declared in a branch which matches {@link ASTSwitchLabel#isCaseNull()}.
     * Any switch that does not have this branch throws NullPointerException
     * at runtime if the scrutinee is null.
     * This is a feature of Java 25.
     */
    default boolean isNullTolerant() {
        return getBranches().map(ASTSwitchBranch::getLabel).any(ASTSwitchLabel::isCaseNull);
    }
}
