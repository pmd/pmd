/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules.optimization;

import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.ast.ASTFormalParameter;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.AccessNode;
import net.sourceforge.pmd.symboltable.NameOccurrence;
import net.sourceforge.pmd.symboltable.Scope;
import net.sourceforge.pmd.symboltable.VariableNameDeclaration;

public class MethodArgumentCouldBeFinal extends AbstractOptimizationRule {

	@Override
    public Object visit(ASTMethodDeclaration meth, Object data) {
        if (meth.isNative() || meth.isAbstract()) {
            return data;
        }
        this.lookForViolation(meth.getScope(),data);
        return super.visit(meth,data);
    }

	private void lookForViolation(Scope scope, Object data) {
        Map<VariableNameDeclaration, List<NameOccurrence>> decls = scope.getVariableDeclarations();
        for (Map.Entry<VariableNameDeclaration, List<NameOccurrence>> entry: decls.entrySet()) {
            VariableNameDeclaration var = entry.getKey();
            AccessNode node = var.getAccessNodeParent();
            if (!node.isFinal() && (node instanceof ASTFormalParameter) && !assigned(entry.getValue())) {
                addViolation(data, node, var.getImage());
            }
        }
	}

	@Override
	public Object visit(ASTConstructorDeclaration constructor, Object data) {
		this.lookForViolation(constructor.getScope(), data);
		return super.visit(constructor,data);
	}

}
