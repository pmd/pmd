/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.design;

import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.NameOccurrence;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;

public class AvoidReassigningParametersRule extends AbstractJavaRule {

	@Override
    public Object visit(ASTMethodDeclarator node, Object data) {
        Map<VariableNameDeclaration, List<NameOccurrence>> params = node.getScope().getVariableDeclarations();
        this.lookForViolation(params, data);
        return super.visit(node, data);
    }

	private void lookForViolation(Map<VariableNameDeclaration,List<NameOccurrence>> params,Object data) {
        for (Map.Entry<VariableNameDeclaration, List<NameOccurrence>> entry: params.entrySet()) {
            VariableNameDeclaration decl = entry.getKey();
            List<NameOccurrence> usages = entry.getValue();
            for (NameOccurrence occ: usages) {
                if ((occ.isOnLeftHandSide() || occ.isSelfAssignment()) &&
                    occ.getNameForWhichThisIsAQualifier() == null &&
                    (! occ.useThisOrSuper()) &&
                    (!decl.isArray() || occ.getLocation().jjtGetParent().jjtGetParent().jjtGetNumChildren() == 1))
                {
                    // not an array or no primary suffix to access the array values
                    addViolation(data, decl.getNode(), decl.getImage());
                }
            }
        }
	}

    @Override
    public Object visit(ASTConstructorDeclaration node,Object data) {
    	Map<VariableNameDeclaration,List<NameOccurrence>> params = node.getScope().getVariableDeclarations();
    	this.lookForViolation(params, data);
    	return super.visit(node,data);
    }
}
