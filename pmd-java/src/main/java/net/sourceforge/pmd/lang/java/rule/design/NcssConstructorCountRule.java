/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTExplicitConstructorInvocation;
import net.sourceforge.pmd.stat.DataPoint;
import net.sourceforge.pmd.util.NumericConstants;

/**
 * Non-commented source statement counter for constructors.
 *
 * @author Jason Bennett
 */
@Deprecated
public class NcssConstructorCountRule extends AbstractNcssCountRule {

    /**
     * Count constructor declarations. This includes any explicit super() calls.
     */
    public NcssConstructorCountRule() {
        super(ASTConstructorDeclaration.class);
        setProperty(MINIMUM_DESCRIPTOR, 100d);
    }

    @Override
    public Object visit(ASTExplicitConstructorInvocation node, Object data) {
        return NumericConstants.ONE;
    }

    @Override
    public Object[] getViolationParameters(DataPoint point) {
        // TODO need to put class name or constructor ID in string
        return new String[] { String.valueOf(((ASTConstructorDeclaration) point.getNode()).getArity()),
            String.valueOf((int) point.getScore()), };
    }
}
