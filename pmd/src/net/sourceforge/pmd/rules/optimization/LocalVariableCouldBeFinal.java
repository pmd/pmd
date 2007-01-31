/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules.optimization;

import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.symboltable.NameOccurrence;
import net.sourceforge.pmd.symboltable.Scope;
import net.sourceforge.pmd.symboltable.VariableNameDeclaration;

public class LocalVariableCouldBeFinal extends AbstractOptimizationRule {

    public Object visit(ASTLocalVariableDeclaration node, Object data) {
        if (node.isFinal()) {
            return data;
        }
        Scope s = node.getScope();
        Map<VariableNameDeclaration, List<NameOccurrence>> decls = s.getVariableDeclarations();
        for (Map.Entry<VariableNameDeclaration, List<NameOccurrence>> entry: decls.entrySet()) {
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
