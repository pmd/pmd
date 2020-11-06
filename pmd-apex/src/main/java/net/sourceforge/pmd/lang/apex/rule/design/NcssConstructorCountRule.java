/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.design;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.stat.DataPoint;
import net.sourceforge.pmd.util.NumericConstants;

/**
 * Non-commented source statement counter for constructors.
 *
 * @author ported from Java original by Jason Bennett
 */
public class NcssConstructorCountRule extends AbstractNcssCountRule {

    /**
     * Count constructor declarations. This includes any explicit super() calls.
     */
    public NcssConstructorCountRule() {
        super(ASTMethod.class);
        setProperty(MINIMUM_DESCRIPTOR, 20d);
        setProperty(CODECLIMATE_CATEGORIES, "Complexity");
        setProperty(CODECLIMATE_REMEDIATION_MULTIPLIER, 50);
        setProperty(CODECLIMATE_BLOCK_HIGHLIGHTING, false);
    }

    @Override
    public Object visit(ASTMethod node, Object data) {
        if (node.isConstructor()) {
            return super.visit(node, data);
        }

        return NumericConstants.ZERO;
    }

    @Override
    public Object[] getViolationParameters(DataPoint point) {
        // TODO need to put class name or constructor ID in string
        return new String[] { String.valueOf((int) point.getScore()) };
    }
}
