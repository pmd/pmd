package net.sourceforge.pmd.rules.strictexception;

import java.util.List;
import java.util.ArrayList;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTTryStatement;
import net.sourceforge.pmd.ast.ASTCatch;
import net.sourceforge.pmd.ast.ASTType;
import net.sourceforge.pmd.ast.ASTName;

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
        ASTType type = getCatchTypeDeclaration(aCatch);
        ASTName name = getTypeNameDeclaration(type);

        if (name.getImage().equals("Throwable")) {
            ruleContext.getReport().addRuleViolation(createRuleViolation(ruleContext, name.getBeginLine()));
        }
    }

    /**
     * @param type catch statememt
     * @return Name of the given type
     */
    private ASTName getTypeNameDeclaration(ASTType type) {
        List myList = new ArrayList(1);
        type.findChildrenOfType(ASTName.class, myList);

        return (ASTName) myList.get(0);
    }

    /**
     * Retrieves the type from the catch statement.
     * @param theCatch statement
     * @return the type of the catch statement
     */
    private ASTType getCatchTypeDeclaration(ASTCatch theCatch) {
        List myList = new ArrayList(1);
        theCatch.getFormalParameter().findChildrenOfType(ASTType.class, myList);
        // Catch declaration can only have one
        // parameter
        return (ASTType) myList.get(0);
    }
}
