/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.complexity;

import java.util.Arrays;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.stat.DataPoint;
import net.sourceforge.pmd.util.NumericConstants;

/**
 * Non-commented source statement counter for methods.
 * 
 * @author ported from Java original of Jason Bennett
 */
public class NcssMethodCountRule extends AbstractNcssCountRule {

    /**
     * Count the size of all non-constructor methods.
     */
    public NcssMethodCountRule() {
        super(ASTMethod.class);
        setProperty(MINIMUM_DESCRIPTOR, 40d);
        setProperty(CODECLIMATE_CATEGORIES, Arrays.asList("Complexity"));
        setProperty(CODECLIMATE_REMEDIATION_MULTIPLIER, 50);
        setProperty(CODECLIMATE_BLOCK_HIGHLIGHTING, false);
    }

    @Override
    public Object visit(ASTMethod node, Object data) {
        if (!node.getNode().getMethodInfo().isConstructor()) {
            return super.visit(node, data);
        }

        return NumericConstants.ZERO;
    }

    @Override
    public Object[] getViolationParameters(DataPoint point) {
        return new String[] { ((ASTMethod) point.getNode()).getNode().getMethodInfo().getName(),
            String.valueOf((int) point.getScore()), };
    }
}
