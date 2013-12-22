/*
 * AssignmentToNonFinalStaticRule.java
 *
 * Created on October 24, 2004, 8:56 AM
 */

package net.sourceforge.pmd.lang.java.rule.design;

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
 */
public class AssignmentToNonFinalStaticRule extends AbstractJavaRule {

    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        Map<VariableNameDeclaration, List<NameOccurrence>> vars = node.getScope().getDeclarations(VariableNameDeclaration.class);
        for (Map.Entry<VariableNameDeclaration, List<NameOccurrence>> entry: vars.entrySet()) {
            VariableNameDeclaration decl = entry.getKey();
            AccessNode accessNodeParent = (AccessNode)decl.getAccessNodeParent();
            if (!accessNodeParent.isStatic() || accessNodeParent.isFinal()) {
                continue;
            }

            if (initializedInConstructor(entry.getValue())) {
                addViolation(data, decl.getNode(), decl.getImage());
            }
        }
        return super.visit(node, data);
    }

    private boolean initializedInConstructor(List<NameOccurrence> usages) {
        boolean initInConstructor = false;

        for (NameOccurrence occ: usages) {
            if (((JavaNameOccurrence)occ).isOnLeftHandSide()) { // specifically omitting prefix and postfix operators as there are legitimate usages of these with static fields, e.g. typesafe enum pattern.
        	Node node = occ.getLocation();
        	Node constructor = node.getFirstParentOfType(ASTConstructorDeclaration.class);
                if (constructor != null) {
                    initInConstructor = true;
                }
            }
        }

        return initInConstructor;
    }

}
