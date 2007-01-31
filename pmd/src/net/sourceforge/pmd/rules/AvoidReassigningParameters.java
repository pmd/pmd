/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules;

import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.symboltable.NameOccurrence;
import net.sourceforge.pmd.symboltable.VariableNameDeclaration;

public class AvoidReassigningParameters extends AbstractRule {

    public Object visit(ASTMethodDeclarator node, Object data) {
        Map<VariableNameDeclaration, List<NameOccurrence>> params = node.getScope().getVariableDeclarations();
        for (Map.Entry<VariableNameDeclaration, List<NameOccurrence>> entry: params.entrySet()) {
            VariableNameDeclaration decl = entry.getKey();
            List<NameOccurrence> usages = entry.getValue();
            for (NameOccurrence occ: usages) {
                if ((occ.isOnLeftHandSide() || occ.isSelfAssignment()) && 
                    occ.getNameForWhichThisIsAQualifier() == null &&
                    (!decl.isArray() || occ.getLocation().jjtGetParent().jjtGetParent().jjtGetNumChildren() == 1))
                {
                    // not an array or no primary suffix to access the array values
                    addViolation(data, decl.getNode(), decl.getImage());
                }
            }
        }
        return super.visit(node, data);
    }
}
