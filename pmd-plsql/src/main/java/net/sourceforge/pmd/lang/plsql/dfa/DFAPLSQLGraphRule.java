/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.dfa;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.lang.dfa.DFAGraphMethod;
import net.sourceforge.pmd.lang.dfa.DFAGraphRule;
import net.sourceforge.pmd.lang.plsql.ast.ASTInput;
import net.sourceforge.pmd.lang.plsql.ast.ASTProgramUnit;
import net.sourceforge.pmd.lang.plsql.ast.ASTTriggerTimingPointSection;
import net.sourceforge.pmd.lang.plsql.ast.ASTTriggerUnit;
import net.sourceforge.pmd.lang.plsql.ast.ASTTypeMethod;
import net.sourceforge.pmd.lang.plsql.rule.AbstractPLSQLRule;

/**
 * @deprecated Only used by the deprecated designer
 */
@Deprecated
public class DFAPLSQLGraphRule extends AbstractPLSQLRule implements DFAGraphRule {

    private List<DFAGraphMethod> executables;

    public DFAPLSQLGraphRule() {
        super.setDfa(true);
    }

    @Override
    public List<DFAGraphMethod> getMethods() {
        return this.executables;
    }

    @Override
    public Object visit(ASTTriggerUnit node, Object data) {
        executables.add(node);
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTProgramUnit node, Object data) {
        executables.add(node);
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTTypeMethod node, Object data) {
        executables.add(node);
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTTriggerTimingPointSection node, Object data) {
        executables.add(node);
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTInput acu, Object data) {
        executables = new ArrayList<>();
        return super.visit(acu, data);
    }
}
