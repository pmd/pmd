/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.ast.ASTInitializer;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.ast.AccessNode;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.symboltable.ClassScope;
import net.sourceforge.pmd.symboltable.MethodNameDeclaration;
import net.sourceforge.pmd.symboltable.NameOccurrence;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class UnusedPrivateMethodRule extends AbstractRule {

    public Object visit(ASTClassOrInterfaceDeclaration node, Object data) {
        if (node.isInterface()) {
            return data;
        }

        Map methods = ((ClassScope)node.getScope()).getMethodDeclarations();
        for (Iterator i = methods.keySet().iterator(); i.hasNext();) {
            MethodNameDeclaration mnd = (MethodNameDeclaration)i.next();
            List occs = (List)methods.get(mnd);
            if (!privateAndNotExcluded(mnd)) {
                continue;
            }
            if (occs.isEmpty()) {
                addViolation(data, mnd.getNode(), mnd.getImage() + mnd.getParameterDisplaySignature());
            } else {
                if (calledFromOutsideItself(occs, mnd)) {
                    addViolation(data, mnd.getNode(), mnd.getImage() + mnd.getParameterDisplaySignature());
                }

            }
        }
        return data;
    }

    private boolean calledFromOutsideItself(List occs, MethodNameDeclaration mnd) {
        int callsFromOutsideMethod = 0;
        for (Iterator i = occs.iterator(); i.hasNext();) {
            NameOccurrence occ = (NameOccurrence)i.next();
            SimpleNode occNode = occ.getLocation();
            ASTConstructorDeclaration enclosingConstructor = (ASTConstructorDeclaration)occNode.getFirstParentOfType(ASTConstructorDeclaration.class);
            if (enclosingConstructor != null) {
                callsFromOutsideMethod++;
                break; // Do we miss unused private constructors here?
            }
            ASTInitializer enclosingInitializer = (ASTInitializer)occNode.getFirstParentOfType(ASTInitializer.class);
            if (enclosingInitializer != null) {
                callsFromOutsideMethod++;
                break;
            }

            ASTMethodDeclaration enclosingMethod = (ASTMethodDeclaration)occNode.getFirstParentOfType(ASTMethodDeclaration.class);
            if ((enclosingMethod == null) || (enclosingMethod != null && !mnd.getNode().jjtGetParent().equals(enclosingMethod))) {
               callsFromOutsideMethod++;
            }
        }
        return callsFromOutsideMethod == 0;
    }

    private boolean privateAndNotExcluded(MethodNameDeclaration mnd) {
        ASTMethodDeclarator node = (ASTMethodDeclarator)mnd.getNode();
        return ((AccessNode) node.jjtGetParent()).isPrivate() && !node.getImage().equals("readObject") && !node.getImage().equals("writeObject") && !node.getImage().equals("readResolve") && !node.getImage().equals("writeReplace");
    }
}
