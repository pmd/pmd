/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.ast;

import java.util.List;

import net.sourceforge.pmd.lang.apex.rule.AbstractApexRule;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;

/**
 * Base class with utility methods for optimization rules
 *
 * @author ported from the Java version of mgriffa
 */
public class AbstractOptimizationRule extends AbstractApexRule {

    public Object visit(ASTUserClass node, Object data) {
        return super.visit(node, data);
    }
    
    public Object visit(ASTUserInterface node, Object data) {
            return data;
    }

    protected boolean assigned(List<NameOccurrence> usages) {
        for (NameOccurrence occ: usages) {
            /*
            JavaNameOccurrence jocc = (JavaNameOccurrence)occ;
            
            if (jocc.isOnLeftHandSide() || jocc.isSelfAssignment()) {
                return true;
            }*/
        }
        return false;
    }

}
