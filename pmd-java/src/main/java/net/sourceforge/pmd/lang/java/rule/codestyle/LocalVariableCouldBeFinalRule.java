/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import static net.sourceforge.pmd.properties.PropertyFactory.booleanProperty;

import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.rule.performance.AbstractOptimizationRule;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;
import net.sourceforge.pmd.lang.symboltable.Scope;
import net.sourceforge.pmd.properties.PropertyDescriptor;

public class LocalVariableCouldBeFinalRule extends AbstractOptimizationRule {

    private static final PropertyDescriptor<Boolean> IGNORE_FOR_EACH =
            booleanProperty("ignoreForEachDecl").defaultValue(false).desc("Ignore non-final loop variables in a for-each statement.").build();

    public LocalVariableCouldBeFinalRule() {
        definePropertyDescriptor(IGNORE_FOR_EACH);
    }

    @Override
    public Object visit(ASTLocalVariableDeclaration node, Object data) {
        if (node.isFinal()) {
            return data;
        }
        if (getProperty(IGNORE_FOR_EACH) && node.getParent() instanceof ASTForStatement) {
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
