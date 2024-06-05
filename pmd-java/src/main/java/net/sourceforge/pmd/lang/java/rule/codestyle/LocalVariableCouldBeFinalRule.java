/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import static net.sourceforge.pmd.properties.PropertyFactory.booleanProperty;

import net.sourceforge.pmd.lang.java.ast.ASTForeachStatement;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableId;
import net.sourceforge.pmd.lang.java.ast.internal.JavaAstUtils;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.properties.PropertyDescriptor;

public class LocalVariableCouldBeFinalRule extends AbstractJavaRulechainRule {

    private static final PropertyDescriptor<Boolean> IGNORE_FOR_EACH =
        booleanProperty("ignoreForEachDecl").defaultValue(false).desc("Ignore non-final loop variables in a for-each statement.").build();

    public LocalVariableCouldBeFinalRule() {
        super(ASTLocalVariableDeclaration.class);
        definePropertyDescriptor(IGNORE_FOR_EACH);
    }

    @Override
    public Object visit(ASTLocalVariableDeclaration node, Object data) {
        if (node.isFinal()) { // also for implicit finals, like resources
            return data;
        }
        if (getProperty(IGNORE_FOR_EACH) && node.getParent() instanceof ASTForeachStatement) {
            return data;
        }
        if (node.getVarIds().all(JavaAstUtils::isEffectivelyFinal)) {
            // All variables declared in this ASTLocalVariableDeclaration need to be
            // effectively final, otherwise we cannot just add a final modifier.
            for (ASTVariableId vid : node.getVarIds()) {
                if (!JavaAstUtils.isNeverUsed(vid)) {
                    // filter out unused variables
                    asCtx(data).addViolation(vid, vid.getName());
                }
            }
        }
        return data;
    }

}
