/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.optimizations;

import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.symboltable.NameOccurrence;
import net.sourceforge.pmd.lang.java.symboltable.Scope;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;

public class LocalVariableCouldBeFinalRule extends AbstractOptimizationRule {

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
                addViolation(data, (Node)var.getAccessNodeParent(), var.getImage());
            }
        }
        return data;
    }

}
