package net.sourceforge.pmd.rules.strictexception;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTCatch;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.ast.ASTTryStatement;
import net.sourceforge.pmd.ast.ASTType;

/**
 * PMD rule which is going to find <code>catch</code> statements
 * containing <code>throwable</code> as the type definition.
 * <p/>
 *
 * @author <a mailto:trondandersen@c2i.net>Trond Andersen</a>
 */
public class AvoidCatchingThrowable extends AbstractRule {


    public Object visit(ASTTryStatement tryStmt, Object o) {
        // Requires a catch statement
        if (!tryStmt.hasCatch()) {
            return super.visit(tryStmt, o);
        }

        /* Checking all catch statements */
        for (int i = 0; i < tryStmt.getCatchBlocks().size(); i++) {
            evaluateCatch((ASTCatch) tryStmt.getCatchBlocks().get(i), (RuleContext) o);
        }
        return super.visit(tryStmt, o);
    }

    /**
     * Checking the catch statement
     *
     * @param aCatch      CatchBlock
     * @param ruleContext
     */
    private void evaluateCatch(ASTCatch aCatch, RuleContext ruleContext) {
        ASTType type = (ASTType) aCatch.getFormalParameter().findChildrenOfType(ASTType.class).get(0);
        ASTClassOrInterfaceType name = (ASTClassOrInterfaceType) type.findChildrenOfType(ASTClassOrInterfaceType.class).get(0);

        if (name.getImage().equals("Throwable")) {
            ruleContext.getReport().addRuleViolation(createRuleViolation(ruleContext, name));
        }
    }
}
