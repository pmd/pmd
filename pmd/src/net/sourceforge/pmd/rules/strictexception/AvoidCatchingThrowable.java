package net.sourceforge.pmd.rules.strictexception;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTCatch;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTTryStatement;
import net.sourceforge.pmd.ast.ASTType;

/**
 * PMD rule which is going to find <code>catch</code> statements
 * containing <code>throwable</code> as the type definition.
 * <p>
 * @author <a mailto:trondandersen@c2i.net>Trond Andersen</a>
 */
public class AvoidCatchingThrowable extends AbstractRule {


    public Object visit(ASTTryStatement astTryStatement, Object o) {
        // Requires a catch statement
        if (!astTryStatement.hasCatch()) {
            return super.visit(astTryStatement, o);
        }

        /* Checking all catch statements */
        for (int i = 0; i < astTryStatement.getCatchBlocks().size(); i++) {
            evaluateCatch((ASTCatch) astTryStatement.getCatchBlocks().get(i), (RuleContext) o);
        }
        return super.visit(astTryStatement, o);
    }

    /**
     * Checking the catch statement
     * @param aCatch CatchBlock
     * @param ruleContext
     */
    private void evaluateCatch(ASTCatch aCatch, RuleContext ruleContext) {
        ASTType type = (ASTType)aCatch.getFormalParameter().findChildrenOfType(ASTType.class).get(0);
        ASTName name = (ASTName)type.findChildrenOfType(ASTName.class).get(0);

        if (name.getImage().equals("Throwable")) {
            ruleContext.getReport().addRuleViolation(createRuleViolation(ruleContext, name.getBeginLine()));
        }
    }
}
