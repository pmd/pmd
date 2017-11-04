/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.rule.design;

import net.sourceforge.pmd.lang.plsql.ast.OracleObject;

/**
 * This rule detects when an Oracle object exceeds a certain threshold. i.e. if
 * a object has more than 1000 lines of code.
 */
public class ExcessiveObjectLengthRule extends ExcessiveLengthRule {
    public ExcessiveObjectLengthRule() {
        super(OracleObject.class);
        setProperty(MINIMUM_DESCRIPTOR, 1000d);
    }
}
