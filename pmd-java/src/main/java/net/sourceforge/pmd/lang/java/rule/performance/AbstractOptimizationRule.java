/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.performance;

import java.util.List;

import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.JavaNameOccurrence;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;

/**
 * Base class with utility methods for optimization rules
 *
 * @author mgriffa
 * @since Created on Jan 11, 2005
 */
public class AbstractOptimizationRule extends AbstractJavaRule {

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (node.isInterface()) {
            return data;
        }
        return super.visit(node, data);
    }

    protected boolean assigned(List<NameOccurrence> usages) {
        for (NameOccurrence occ : usages) {
            JavaNameOccurrence jocc = (JavaNameOccurrence) occ;
            if (jocc.isOnLeftHandSide() || jocc.isSelfAssignment()) {
                return true;
            }
        }
        return false;
    }

}
