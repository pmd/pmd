/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.ast.ASTDoStatement;
import net.sourceforge.pmd.ast.ASTForStatement;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.ASTTryStatement;
import net.sourceforge.pmd.ast.ASTVariableInitializer;
import net.sourceforge.pmd.ast.ASTWhileStatement;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.symboltable.NameOccurrence;
import net.sourceforge.pmd.symboltable.VariableNameDeclaration;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author Olander
 */
public class ImmutableField extends AbstractRule {
    
    private static final int MUTABLE = 0;
    private static final int IMMUTABLE = 1;
    private static final int CHECKDECL = 2;

    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        Map vars = node.getScope().getVariableDeclarations();
        Set constructors = findAllConstructors(node);
        for (Iterator i = vars.keySet().iterator(); i.hasNext();) {
            VariableNameDeclaration field = (VariableNameDeclaration) i.next();
            if (field.getAccessNodeParent().isStatic() || !field.getAccessNodeParent().isPrivate() || field.getAccessNodeParent().isFinal()) {
                continue;
            }

            int result = initializedInConstructor((List)vars.get(field), new HashSet(constructors));
            if (result == MUTABLE) {
                continue;
            }
            if (result == IMMUTABLE || ((result == CHECKDECL) && !field.getAccessNodeParent().findChildrenOfType(ASTVariableInitializer.class).isEmpty())) {
                addViolation(data, field.getNode(), field.getImage());
            }
        }
        return super.visit(node, data);
    }

    private int initializedInConstructor(List usages, Set allConstructors) {
        int rc = MUTABLE, methodInitCount = 0;
        Set consSet = new HashSet();
        for (Iterator j = usages.iterator(); j.hasNext();) {
            NameOccurrence occ = (NameOccurrence)j.next();
            if (occ.isOnLeftHandSide() || occ.isSelfAssignment()) {
                SimpleNode node = occ.getLocation();
                SimpleNode constructor = (SimpleNode)node.getFirstParentOfType(ASTConstructorDeclaration.class);
                if (constructor != null) {
                    if (inLoopOrTry(node)) {
                        continue;
                    }
                    consSet.add(constructor);
                } else {
                    if (node.getFirstParentOfType(ASTMethodDeclaration.class) != null) {
                        methodInitCount++;
                    }
                }
            }
        }
        if (usages.isEmpty() || ((methodInitCount == 0) && consSet.isEmpty())) {
            rc = CHECKDECL;
        } else {
            allConstructors.removeAll(consSet);
            if (allConstructors.isEmpty() && (methodInitCount == 0)) {
                rc = IMMUTABLE;
            }
        }
        return rc;
    }

    private boolean inLoopOrTry(SimpleNode node) {
        return (SimpleNode)node.getFirstParentOfType(ASTTryStatement.class) != null ||
               (SimpleNode)node.getFirstParentOfType(ASTForStatement.class) != null ||
               (SimpleNode)node.getFirstParentOfType(ASTWhileStatement.class) != null ||
               (SimpleNode)node.getFirstParentOfType(ASTDoStatement.class) != null;
    }

    /** construct a set containing all ASTConstructorDeclaration nodes for this class
     */
    private Set findAllConstructors(ASTClassOrInterfaceDeclaration node) {
        Set set = new HashSet();
        set.addAll(node.findChildrenOfType(ASTConstructorDeclaration.class));
        return set;
    }
}
