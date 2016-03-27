/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.rule.codesize;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.stat.DataPoint;

/**
 * Non-commented source statement counter for methods.
 * 
 * @author Jason Bennett
 */
public class NcssMethodCountRule extends AbstractNcssCountRule {

    /**
     * Count the size of all non-constructor methods.
     */
    public NcssMethodCountRule() {
        super(ASTMethod.class);
        setProperty(MINIMUM_DESCRIPTOR, 100d);
    }

    @Override
    public Object visit(ASTMethod node, Object data) {
        return super.visit(node, data);
    }

    @Override
    public Object[] getViolationParameters(DataPoint point) {
        return new String[] { ((ASTMethod) point.getNode()).getNode().getMethodInfo().getName(),
                String.valueOf((int) point.getScore()) };
    }
}
