/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design;

import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTThrowStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableAccess;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.rule.internal.DataflowPass;
import net.sourceforge.pmd.lang.java.rule.internal.DataflowPass.AssignmentEntry;
import net.sourceforge.pmd.lang.java.rule.internal.DataflowPass.ReachingDefinitionSet;
import net.sourceforge.pmd.lang.java.symbols.JLocalVariableSymbol;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;

/**
 * Finds <code>throw</code> statements containing <code>NullPointerException</code>
 * instances as thrown values
 *
 * @author <a href="mailto:michaeller.2012@gmail.com">Mykhailo Palahuta</a>
 */
public class AvoidThrowingNullPointerExceptionRule extends AbstractJavaRulechainRule {

    public AvoidThrowingNullPointerExceptionRule() {
        super(ASTThrowStatement.class);
    }

    @Override
    public Object visit(ASTThrowStatement throwStmt, Object data) {
        ASTExpression thrown = throwStmt.getExpr();
        if (TypeTestUtil.isA(NullPointerException.class, thrown)) {
            addViolation(data, throwStmt);
        } else if (thrown instanceof ASTVariableAccess) {
            JVariableSymbol sym = ((ASTVariableAccess) thrown).getReferencedSym();
            if (sym instanceof JLocalVariableSymbol
                && hasNpeValue((ASTVariableAccess) thrown, (JLocalVariableSymbol) sym)) {
                addViolation(data, throwStmt);
            }
        }
        return null;
    }

    private boolean hasNpeValue(ASTVariableAccess thrown, JLocalVariableSymbol sym) {
        DataflowPass.ensureProcessed(thrown.getRoot());
        ReachingDefinitionSet reaching = DataflowPass.getReachingDefinitions(thrown);
        if (reaching == null || reaching.isNotFullyKnown()) {
            // we lean towards false negatives... maybe we should be able
            // to report this with a lower priority
            return false;
        }

        for (AssignmentEntry it : reaching.getReaching()) {
            if (!TypeTestUtil.isExactlyA(NullPointerException.class, it.getRhsType())) {
                return false;
            }
        }
        return true;
    }
}
