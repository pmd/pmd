/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.stat.DataPoint;

/**
 * Non-commented source statement counter for methods.
 *
 * @author Jason Bennett
 */
@Deprecated
public class NcssMethodCountRule extends AbstractNcssCountRule {

    /**
     * Count the size of all non-constructor methods.
     */
    public NcssMethodCountRule() {
        super(ASTMethodDeclaration.class);
        setProperty(MINIMUM_DESCRIPTOR, 100d);
    }

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {
        return super.visit(node, data);
    }

    @Override
    public Object[] getViolationParameters(DataPoint point) {
        return new String[] {((ASTMethodDeclaration) point.getNode()).getName(),
                             String.valueOf((int) point.getScore()), };
    }
}
