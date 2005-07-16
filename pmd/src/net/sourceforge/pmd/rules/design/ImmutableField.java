/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.ASTTryStatement;
import net.sourceforge.pmd.ast.ASTVariableInitializer;
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
    
    static private final int MUTABLE = 0;
    static private final int IMMUTABLE = 1;
    static private final int CHECKDECL = 2;
    
    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        Map vars = node.getScope().getVariableDeclarations();
        Set constructors = findAllConstructors(node);
        for (Iterator i = vars.keySet().iterator(); i.hasNext();) {
            VariableNameDeclaration decl = (VariableNameDeclaration) i.next();
            if (decl.getAccessNodeParent().isStatic() || !decl.getAccessNodeParent().isPrivate() || decl.getAccessNodeParent().isFinal()) {
                continue;
            }
            
            int result = initializedInConstructor((List)vars.get(decl), new HashSet(constructors));
            if (result == MUTABLE) {
                continue;
            }
            if (result == IMMUTABLE || ((result == CHECKDECL) && initializedInDeclaration(decl.getAccessNodeParent()))) {
                addViolation(data, decl.getNode(), decl.getImage());
            }
        }
        return super.visit(node, data);
    }
    
    private int initializedInConstructor(List usages, Set allConstructors) {
        int rc = MUTABLE, methodInitCount = 0;
        boolean foundUsage = false;
        Set consSet = new HashSet();

        for (Iterator j = usages.iterator(); j.hasNext();) {
            foundUsage = true;
            NameOccurrence occ = (NameOccurrence)j.next();
            if (occ.isOnLeftHandSide() || occ.isSelfAssignment()) {
                SimpleNode node = occ.getLocation();
                if ((SimpleNode)node.getFirstParentOfType(ASTTryStatement.class) != null) {
                    continue;
                }
                SimpleNode constructor = (SimpleNode)node.getFirstParentOfType(ASTConstructorDeclaration.class);
                if (constructor != null) {
                    consSet.add(constructor);
                } else {
                    if (node.getFirstParentOfType(ASTMethodDeclaration.class) != null) {
                        methodInitCount++;
                    }
                }
            }
        }
        if (!foundUsage || ((methodInitCount == 0) && consSet.isEmpty())) {
            rc = CHECKDECL;
        } else {
            allConstructors.removeAll(consSet);
            if (allConstructors.isEmpty() && (methodInitCount == 0)) {
                rc = IMMUTABLE;
            }
        }
        return rc;
    }

    private boolean initializedInDeclaration(SimpleNode node) {
        return !node.findChildrenOfType(ASTVariableInitializer.class).isEmpty();
    }

    /** construct a set containing all ASTConstructorDeclaration nodes for this class
     */
    private Set findAllConstructors(ASTClassOrInterfaceDeclaration node) {
        Set set = new HashSet();
        set.addAll(node.findChildrenOfType(ASTConstructorDeclaration.class));
        return set;
    }
}
