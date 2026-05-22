/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import static net.sourceforge.pmd.properties.PropertyFactory.booleanProperty;

import net.sourceforge.pmd.lang.java.ast.ASTForInit;
import net.sourceforge.pmd.lang.java.ast.ASTForeachStatement;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableId;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.reporting.RuleContext;

public class LocalVariableCouldBeFinalRule extends AbstractJavaRulechainRule {

    private static final PropertyDescriptor<Boolean> IGNORE_FOR_EACH =
        booleanProperty("ignoreForEachDecl").defaultValue(false).desc("Ignore non-final loop variables in a for-each statement.").build();

    public LocalVariableCouldBeFinalRule() {
        super(ASTLocalVariableDeclaration.class);
        definePropertyDescriptor(IGNORE_FOR_EACH);
    }

    @Override
    public Object visit(ASTLocalVariableDeclaration node, Object data) {
        RuleContext ctx = (RuleContext) data;

        if (node.isFinal()) { // also for implicit finals, like resources, or lombok.val
            return ctx;
        }
        if (getProperty(IGNORE_FOR_EACH) && node.getParent() instanceof ASTForeachStatement) {
            return ctx;
        }
        if (node.getParent() instanceof ASTForInit) {
            return specialCaseForInit(node, ctx);
        }
        for (ASTVariableId vid : node.getVarIds()) {
            if (!JavaAstUtils.isNeverUsed(vid) && JavaAstUtils.isEffectivelyFinal(vid)) {
                ctx.addViolation(vid, vid.getName());
            }
        }
        return null;
    }

    private RuleContext specialCaseForInit(ASTLocalVariableDeclaration node, RuleContext ctx) {
        // See https://github.com/pmd/pmd/issues/1619 for why this is necessary
        if (node.getVarIds().all(JavaAstUtils::isEffectivelyFinal)) {
            // All variables declared in this ASTLocalVariableDeclaration need to be
            // effectively final, otherwise we cannot just add a final modifier.
            for (ASTVariableId vid : node.getVarIds()) {
                if (!JavaAstUtils.isNeverUsed(vid)) {
                    // filter out unused variables
                    ctx.addViolation(vid, vid.getName());
                }
            }
        }
        return ctx;
    }

}
