/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceBodyDeclaration;
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

import java.util.ArrayList;
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
        List constructors = findAllConstructors(node);
        for (Iterator i = vars.entrySet().iterator(); i.hasNext();) {
            Map.Entry entry = (Map.Entry) i.next();
            VariableNameDeclaration field = (VariableNameDeclaration) entry.getKey();
            if (field.getAccessNodeParent().isStatic() || !field.getAccessNodeParent().isPrivate() || field.getAccessNodeParent().isFinal()) {
                continue;
            }

            int result = initializedInConstructor((List) entry.getValue(), new HashSet(constructors));
            if (result == MUTABLE) {
                continue;
            }
            if (result == IMMUTABLE || (result == CHECKDECL && initializedWhenDeclared(field))) {
                addViolation(data, field.getNode(), field.getImage());
            }
        }
        return super.visit(node, data);
    }

    private boolean initializedWhenDeclared(VariableNameDeclaration field) {
        return !field.getAccessNodeParent().findChildrenOfType(ASTVariableInitializer.class).isEmpty();
    }

    private int initializedInConstructor(List usages, Set allConstructors) {
        int result = MUTABLE, methodInitCount = 0;
        Set consSet = new HashSet();
        for (Iterator j = usages.iterator(); j.hasNext();) {
            NameOccurrence occ = (NameOccurrence) j.next();
            if (occ.isOnLeftHandSide() || occ.isSelfAssignment()) {
                SimpleNode node = occ.getLocation();
                SimpleNode constructor = (SimpleNode) node.getFirstParentOfType(ASTConstructorDeclaration.class);
                if (constructor != null) {
                    if (inLoopOrTry(node)) {
                        continue;
                    }
                    if (inAnonymousInnerClass(node)) {
                        methodInitCount++;
                    } else {
                        consSet.add(constructor);
                    }
                } else {
                    if (node.getFirstParentOfType(ASTMethodDeclaration.class) != null) {
                        methodInitCount++;
                    }
                }
            }
        }
        if (usages.isEmpty() || ((methodInitCount == 0) && consSet.isEmpty())) {
            result = CHECKDECL;
        } else {
            allConstructors.removeAll(consSet);
            if (allConstructors.isEmpty() && (methodInitCount == 0)) {
                result = IMMUTABLE;
            }
        }
        return result;
    }

    private boolean inLoopOrTry(SimpleNode node) {
        return (SimpleNode) node.getFirstParentOfType(ASTTryStatement.class) != null ||
                (SimpleNode) node.getFirstParentOfType(ASTForStatement.class) != null ||
                (SimpleNode) node.getFirstParentOfType(ASTWhileStatement.class) != null ||
                (SimpleNode) node.getFirstParentOfType(ASTDoStatement.class) != null;
    }

    private boolean inAnonymousInnerClass(SimpleNode node) {
        ASTClassOrInterfaceBodyDeclaration parent = (ASTClassOrInterfaceBodyDeclaration) node.getFirstParentOfType(ASTClassOrInterfaceBodyDeclaration.class);
        return parent != null && parent.isAnonymousInnerClass();
    }

    private List findAllConstructors(ASTClassOrInterfaceDeclaration node) {
        List cons = new ArrayList();
        node.findChildrenOfType(ASTConstructorDeclaration.class, cons, false);
        return cons;
    }
}
