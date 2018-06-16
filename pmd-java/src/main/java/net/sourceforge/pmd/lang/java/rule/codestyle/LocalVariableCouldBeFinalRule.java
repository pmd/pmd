/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.rule.performance.AbstractOptimizationRule;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;
import net.sourceforge.pmd.lang.symboltable.Scope;

public class LocalVariableCouldBeFinalRule extends AbstractOptimizationRule {

    @Override
    public Object visit(ASTLocalVariableDeclaration node, Object data) {
        if (node.isFinal()) {
            return data;
        }
        Scope s = node.getScope();
        Map<VariableNameDeclaration, List<NameOccurrence>> decls = s.getDeclarations(VariableNameDeclaration.class);
        for (Map.Entry<VariableNameDeclaration, List<NameOccurrence>> entry : decls.entrySet()) {
            VariableNameDeclaration var = entry.getKey();
            if (var.getAccessNodeParent() != node) {
                continue;
            }
            if (!assigned(entry.getValue())) {
                addViolation(data, var.getAccessNodeParent(), var.getImage());
            }
        }
        return data;
    }

}
