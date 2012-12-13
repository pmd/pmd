/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.plsql.rule.codesize;

import net.sourceforge.pmd.lang.plsql.ast.ExecutableCode;
import net.sourceforge.pmd.stat.DataPoint;

/**
 * Non-commented source statement counter for methods.
 * 
 * Analogous to and cribbed from {@link net.sourceforge.pmd.lang.java.rule.codesize.NcssMethodCountRule}.
 */
public class NcssMethodCountRule extends AbstractNcssCountRule {

    /**
     * Count the size of all non-constructor methods.
     */
    public NcssMethodCountRule() {
	super(ExecutableCode.class);
	setProperty(MINIMUM_DESCRIPTOR, 100d);
    }

    @Override
    public Object visit(ExecutableCode node, Object data) {
	return super.visit(node, data);
    }

    @Override
    public Object[] getViolationParameters(DataPoint point) {
	return new String[] { ((ExecutableCode) point.getNode()).getMethodName(),
		String.valueOf((int) point.getScore()) };
    }
}
