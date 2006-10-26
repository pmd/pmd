/*
 * SingularField.java
 *
 * Created on April 17, 2005, 9:49 PM
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.ast.ASTInitializer;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.symboltable.NameOccurrence;

import java.util.List;

/**
 * @author Eric Olander
 */
public class SingularField extends AbstractRule {

    public Object visit(ASTFieldDeclaration node, Object data) {
        if (node.isPrivate() && !node.isStatic()) {
            List list = node.findChildrenOfType(ASTVariableDeclaratorId.class);
            ASTVariableDeclaratorId declaration = (ASTVariableDeclaratorId) list.get(0);
            List usages = declaration.getUsages();
            SimpleNode decl = null;
            boolean violation = true;
            for (int ix = 0; ix < usages.size(); ix++) {
                NameOccurrence no = (NameOccurrence) usages.get(ix);
                SimpleNode location = no.getLocation();

                SimpleNode method = (SimpleNode) location.getFirstParentOfType(ASTMethodDeclaration.class);
                if (method == null) {
                    method = (SimpleNode) location.getFirstParentOfType(ASTConstructorDeclaration.class);
                    if (method == null) {
                    	method = (SimpleNode) location.getFirstParentOfType(ASTInitializer.class);
                    	if (method == null) {
                    		continue;
                    	}
                    }
                }

                if (decl == null) {
                    decl = method;
                    continue;
                } else if (decl != method) {

                    violation = false;
                }
            }

            if (violation && !usages.isEmpty()) {
                addViolation(data, node, new Object[] { declaration.getImage() });
            }
        }
        return data;
    }
}
