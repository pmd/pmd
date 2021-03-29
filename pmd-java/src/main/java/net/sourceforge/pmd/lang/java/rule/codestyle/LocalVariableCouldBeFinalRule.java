/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import static net.sourceforge.pmd.properties.PropertyFactory.booleanProperty;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.java.ast.ASTForeachStatement;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
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
        MethodArgumentCouldBeFinalRule.checkForFinal((RuleContext) data, this, node.getVarIds());
        return data;
    }

}
