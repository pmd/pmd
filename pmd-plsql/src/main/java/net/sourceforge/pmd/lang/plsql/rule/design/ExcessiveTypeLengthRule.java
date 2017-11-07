/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.rule.design;

import net.sourceforge.pmd.lang.plsql.ast.ASTTypeSpecification;

/**
 * This rule detects when a class exceeds a certain threshold. i.e. if a class
 * has more than 1000 lines of code.
 */
public class ExcessiveTypeLengthRule extends ExcessiveLengthRule {
    public ExcessiveTypeLengthRule() {
        super(ASTTypeSpecification.class);
        setProperty(MINIMUM_DESCRIPTOR, 1000d);
    }
}
