/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
*/
package net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.ast.ASTClassDeclaration;

/**
 * This rule detects when a class exceeds a certain
 * threshold.  i.e. if a class has more than 1000 lines
 * of code.
 */
public class LongClassRule extends ExcessiveLengthRule {
    public LongClassRule() {
        super(ASTClassDeclaration.class);
    }
}
