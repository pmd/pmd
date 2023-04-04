/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.design;

import net.sourceforge.pmd.lang.apex.ast.ASTMethod;

/**
 * Non-commented source statement counter for methods.
 *
 * @author ported from Java original of Jason Bennett
 */
public class NcssMethodCountRule extends AbstractNcssCountRule<ASTMethod> {

    /**
     * Count the size of all non-constructor methods.
     */
    public NcssMethodCountRule() {
        super(ASTMethod.class);
    }

    @Override
    protected int defaultReportLevel() {
        return 40;
    }

    @Override
    protected boolean isIgnored(ASTMethod node) {
        return node.isConstructor();
    }
}
