/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.design;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;

/**
 * Non-commented source statement counter for constructors.
 *
 * @author ported from Java original by Jason Bennett
 */
public class NcssConstructorCountRule extends AbstractNcssCountRule<ASTMethod> {

    /**
     * Count constructor declarations. This includes any explicit super() calls.
     */
    public NcssConstructorCountRule() {
        super(ASTMethod.class);
    }

    @Override
    protected int defaultReportLevel() {
        return 20;
    }

    @Override
    protected boolean isIgnored(ASTMethod node) {
        return !node.isConstructor();
    }
}
