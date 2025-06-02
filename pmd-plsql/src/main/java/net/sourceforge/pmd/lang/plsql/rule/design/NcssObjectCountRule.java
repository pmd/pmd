/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.rule.design;

import net.sourceforge.pmd.lang.plsql.ast.ASTGlobal;
import net.sourceforge.pmd.lang.plsql.ast.ASTProgramUnit;
import net.sourceforge.pmd.lang.plsql.ast.OracleObject;

/**
 * Non-commented source statement counter for Oracle Object declarations.
 *
 * @author Stuart Turton
 */
public class NcssObjectCountRule extends AbstractNcssCountRule<OracleObject> {

    /**
     * Count type declarations. This includes Oracle Objects.
     */
    public NcssObjectCountRule() {
        super(OracleObject.class);
    }

    @Override
    protected int defaultReportLevel() {
        return 1500;
    }

    @Override
    protected boolean isIgnored(OracleObject node) {
        // Treat Schema-level ProgramUnits as Oracle Objects, otherwise as
        // subprograms
        return node instanceof ASTProgramUnit && !(node.getParent() instanceof ASTGlobal);
    }

    @Override
    protected Object[] getViolationParameters(OracleObject node, int metric) {
        String name = node.getObjectName();
        return new Object[] {name == null ? "(unnamed)" : name, metric};
    }
}
