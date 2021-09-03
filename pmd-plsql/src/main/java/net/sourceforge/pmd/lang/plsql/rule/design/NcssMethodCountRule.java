/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.rule.design;

import net.sourceforge.pmd.lang.plsql.ast.ExecutableCode;

/**
 * Non-commented source statement counter for methods.
 *
 * <p>Analogous to and cribbed from Java version of the rule.</p>
 */
public class NcssMethodCountRule extends AbstractNcssCountRule<ExecutableCode> {

    /**
     * Count the size of all non-constructor methods.
     */
    public NcssMethodCountRule() {
        super(ExecutableCode.class);
    }

    @Override
    protected int defaultReportLevel() {
        return 100;
    }

    @Override
    protected Object[] getViolationParameters(ExecutableCode node, int metric) {
        String name = node.getMethodName();
        return new Object[] {name == null ? "(unnamed)" : name, metric};
    }



}
