/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.symboltable.NameOccurrence;
import net.sourceforge.pmd.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.ASTStatement;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class SymbolTableTestRule extends AbstractRule implements Rule {

    public Object visit(ASTStatement node, Object data) {
        Map decls = node.getScope().getVariableDeclarations();
        for (Iterator i = decls.keySet().iterator(); i.hasNext();) {
            VariableNameDeclaration decl = (VariableNameDeclaration) i.next();

            List usages = (List) decls.get(decl);
            if (!isStaticMethodBeingInvoked(usages)) {
                System.out.println("Error");
            }
        }
        return data;
    }

    private boolean isStaticMethodBeingInvoked(List usages) {
        for (Iterator j = usages.iterator(); j.hasNext();) {
            NameOccurrence nameOccurrence = (NameOccurrence) j.next();
            if (nameOccurrence.isPartOfQualifiedName()) {
                System.out.println(nameOccurrence.getNameForWhichThisIsAQualifier().getImage());

// how do i get the method access node here?

/*
if(method is static) {
return true;
}
*/
            }
        }
        return false;
    }
}
