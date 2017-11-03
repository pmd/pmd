/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.rule.design;

import net.sourceforge.pmd.lang.plsql.ast.ASTPackageBody;

/**
 * This rule detects when a class exceeds a certain threshold. i.e. if a class
 * has more than 1000 lines of code.
 */
public class ExcessivePackageBodyLengthRule extends ExcessiveLengthRule {
    public ExcessivePackageBodyLengthRule() {
        super(ASTPackageBody.class);
        setProperty(MINIMUM_DESCRIPTOR, 1000d);
    }
}
