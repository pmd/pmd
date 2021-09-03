/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.design;

import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;

/**
 * Non-commented source statement counter for type declarations.
 *
 * @author ported from Java original of Jason Bennett
 */
public class NcssTypeCountRule extends AbstractNcssCountRule {

    /**
     * Count type declarations. This includes classes as well as enums and
     * annotations.
     */
    public NcssTypeCountRule() {
        super(ASTUserClass.class);
    }

    @Override
    protected int defaultReportLevel() {
        return 500;
    }

}
