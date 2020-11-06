/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.lang.java.ast.AccessNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.JavaNameOccurrence;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;

/**
 * @author Eric Olander
 * @since Created on October 24, 2004, 8:56 AM
 */
public class AssignmentToNonFinalStaticRule extends AbstractJavaRule {

    @Override
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        Map<VariableNameDeclaration, List<NameOccurrence>> vars = node.getScope()
                .getDeclarations(VariableNameDeclaration.class);
        for (Map.Entry<VariableNameDeclaration, List<NameOccurrence>> entry : vars.entrySet()) {
            VariableNameDeclaration decl = entry.getKey();
            AccessNode accessNodeParent = decl.getAccessNodeParent();
            if (!accessNodeParent.isStatic() || accessNodeParent.isFinal()) {
                continue;
            }

            final List<Node> locations = initializedInConstructor(entry.getValue());
            for (final Node location : locations) {
                addViolation(data, location, decl.getImage());
            }
        }
        return super.visit(node, data);
    }

    private List<Node> initializedInConstructor(List<NameOccurrence> usages) {
        final List<Node> unsafeAssignments = new ArrayList<>();
        for (NameOccurrence occ : usages) {
            // specifically omitting prefix and postfix operators as there are
            // legitimate usages of these with static fields, e.g. typesafe enum pattern.
            if (((JavaNameOccurrence) occ).isOnLeftHandSide()) {
                Node node = occ.getLocation();
                Node constructor = node.getFirstParentOfType(ASTConstructorDeclaration.class);
                if (constructor != null) {
                    unsafeAssignments.add(node);
                }
            }
        }

        return unsafeAssignments;
    }

}
