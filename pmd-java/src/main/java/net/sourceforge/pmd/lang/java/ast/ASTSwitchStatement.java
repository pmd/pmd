/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.Iterator;
import java.util.Set;

import org.apache.commons.lang3.EnumUtils;

import net.sourceforge.pmd.annotation.InternalApi;


/**
 * Represents a {@code switch} statement.
 *
 * <pre>
 *    SwitchStatement ::= "switch" "(" {@linkplain ASTExpression Expression} ")" "{"
 *                        ( {@linkplain ASTSwitchLabel SwitchLabel} {@linkplain ASTBlockStatement BlockStatement}* )*
 *                        "}"
 * </pre>
 */
public class ASTSwitchStatement extends AbstractJavaNode implements Iterable<ASTSwitchLabel> {

    @InternalApi
    @Deprecated
    public ASTSwitchStatement(int id) {
        super(id);
    }

    @InternalApi
    @Deprecated
    public ASTSwitchStatement(JavaParser p, int id) {
        super(p, id);
    }


    @Override
    public Object jjtAccept(JavaParserVisitor visitor, Object data) {
        return visitor.visit(this, data);
    }


    /**
     * Returns true if this switch has a {@code default} case.
     */
    public boolean hasDefaultCase() {
        for (ASTSwitchLabel label : this) {
            if (label.isDefault()) {
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

            for (ASTSwitchLabel label : this) {
                // since this is an enum switch, the labels are necessarily
                // the simple name of some enum constant.

                constantNames.remove(label.getFirstDescendantOfType(ASTName.class).getImage());

            }

            return constantNames.isEmpty();
        }

        return false;
    }


    @Override
    public Iterator<ASTSwitchLabel> iterator() {
        return new NodeChildrenIterator<>(this, ASTSwitchLabel.class);
    }
}
