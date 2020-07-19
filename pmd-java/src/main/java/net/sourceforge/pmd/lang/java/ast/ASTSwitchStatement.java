/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Set;

import org.apache.commons.lang3.EnumUtils;

/**
 * Represents a {@code switch} statement. See {@link ASTSwitchLike} for
 * its grammar.
 */
public final class ASTSwitchStatement extends AbstractStatement implements ASTSwitchLike {

    ASTSwitchStatement(int id) {
        super(id);
    }


    @Override
    protected <P, R> R acceptVisitor(JavaVisitor<? super P, ? extends R> visitor, P data) {
        return visitor.visit(this, data);
    }

    /**
     * Returns true if this switch has a {@code default} case.
     */
    public boolean hasDefaultCase() {
        for (ASTSwitchBranch label : this) {
            if (label.getLabel().isDefault()) {
                return true;
            }
        }
        return false;
    }


    /**
     * Gets the expression tested by this switch.
     * This is the expression between the parentheses.
     */
    public ASTExpression getTestedExpression() {
        return (ASTExpression) getChild(0);
    }


    /**
     * Returns true if this switch statement tests an expression
     * having an enum type and all the constants of this type
     * are covered by a switch case. Returns false if the type of
     * the tested expression could not be resolved.
     */
    public boolean isExhaustiveEnumSwitch() {
        ASTExpression expression = getTestedExpression();

        if (expression.getType() == null) {
            return false;
        }

        if (Enum.class.isAssignableFrom(expression.getType())) {

            @SuppressWarnings("unchecked")
            Set<String> constantNames = EnumUtils.getEnumMap((Class<? extends Enum>) expression.getType()).keySet();

            for (ASTSwitchBranch label : this) {
                // since this is an enum switch, the labels are necessarily
                // the simple name of some enum constant.

                constantNames.remove(label.getLabel().getFirstDescendantOfType(ASTName.class).getImage());

            }

            return constantNames.isEmpty();
        }

        return false;
    }
}
