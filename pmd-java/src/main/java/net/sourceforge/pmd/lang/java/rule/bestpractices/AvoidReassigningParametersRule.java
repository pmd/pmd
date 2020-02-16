/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.JavaNameOccurrence;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;

public class AvoidReassigningParametersRule extends AbstractJavaRule {

    @Override
    public Object visit(ASTMethodDeclarator node, Object data) {
        Map<VariableNameDeclaration, List<NameOccurrence>> params = node.getScope()
                .getDeclarations(VariableNameDeclaration.class);
        this.lookForViolation(params, data);
        return super.visit(node, data);
    }

    private void lookForViolation(Map<VariableNameDeclaration, List<NameOccurrence>> params, Object data) {
        for (Map.Entry<VariableNameDeclaration, List<NameOccurrence>> entry : params.entrySet()) {
            VariableNameDeclaration decl = entry.getKey();
            List<NameOccurrence> usages = entry.getValue();

            // Only look for formal parameters
            if (!decl.getDeclaratorId().isFormalParameter()) {
                continue;
            }

            for (NameOccurrence occ : usages) {
                JavaNameOccurrence jocc = (JavaNameOccurrence) occ;
                if ((jocc.isOnLeftHandSide() || jocc.isSelfAssignment())
                        && jocc.getNameForWhichThisIsAQualifier() == null && !jocc.useThisOrSuper() && !decl.isVarargs()
                        && (!decl.isArray()
                                || jocc.getLocation().getParent().getParent().getNumChildren() == 1)) {
                    // not an array or no primary suffix to access the array
                    // values
                    addViolation(data, decl.getNode(), decl.getImage());
                }
            }
        }
    }

    @Override
    public Object visit(ASTConstructorDeclaration node, Object data) {
        Map<VariableNameDeclaration, List<NameOccurrence>> params = node.getScope()
                .getDeclarations(VariableNameDeclaration.class);
        this.lookForViolation(params, data);
        return super.visit(node, data);
    }
}
