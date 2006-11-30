/*
 * Created on Jan 11, 2005 
 *
 * $Id$
 */
package net.sourceforge.pmd.rules.optimization;

import java.util.Iterator;
import java.util.List;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.symboltable.NameOccurrence;

/**
 * Base class with utility methods for optimization rules
 *
 * @author mgriffa
 */
public class AbstractOptimizationRule extends AbstractRule implements Rule {

    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (node.isInterface()) {
            return data;
        }
        return super.visit(node, data);
    }

    protected boolean assigned(List usages) {
        for (Iterator j = usages.iterator(); j.hasNext();) {
            NameOccurrence occ = (NameOccurrence) j.next();
            if (occ.isOnLeftHandSide() || occ.isSelfAssignment()) {
                return true;
            }
            continue;
        }
        return false;
    }

}
