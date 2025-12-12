/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.rule.design;

import net.sourceforge.pmd.lang.plsql.ast.OracleObject;

/**
 * This rule detects when an Oracle object exceeds a certain threshold. i.e. if
 * a object has more than 1000 lines of code.
 *
 * <p>Equivalent XPath: {@code //(PackageBody|PackageSpecification|ProgramUnit|TriggerUnit|TypeSpecification)[@EndLine - @BeginLine > 1000]}</p>
 * @deprecated Since 7.19.0. Use the rule {@link NcssCountRule} instead.
 */
@Deprecated
public class ExcessiveObjectLengthRule extends AbstractCounterCheckRule.AbstractLineLengthCheckRule<OracleObject> {
    public ExcessiveObjectLengthRule() {
        super(OracleObject.class);
    }

    @Override
    protected int defaultReportLevel() {
        return 1000;
    }
}
