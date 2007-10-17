/*
 * SingularField.java
 *
 * Created on April 17, 2005, 9:49 PM
 */
package net.sourceforge.pmd.rules;

import java.util.List;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.ast.ASTExpression;
import net.sourceforge.pmd.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.ast.ASTInitializer;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.ASTSynchronizedStatement;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.ast.Node;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.symboltable.NameOccurrence;

/**
 * @author Eric Olander
 */
public class SingularField extends AbstractRule {
	
    public Object visit(ASTFieldDeclaration node, Object data) {
        if (node.isPrivate() && !node.isStatic()) {
            List<ASTVariableDeclaratorId> list = node.findChildrenOfType(ASTVariableDeclaratorId.class);
            ASTVariableDeclaratorId declaration = list.get(0);
            List<NameOccurrence> usages = declaration.getUsages();
            SimpleNode decl = null;
            boolean violation = true;
            for (int ix = 0; ix < usages.size(); ix++) {
                NameOccurrence no = usages.get(ix);
                SimpleNode location = no.getLocation();

                Node parent3 = location.getNthParent(3);
                if (parent3 instanceof ASTExpression 
                		&& parent3.jjtGetParent() instanceof ASTSynchronizedStatement) {
                	//This usage is directly in an expression of a synchronized block
                	violation = false;
                	break;
                }
                
                SimpleNode method = location.getFirstParentOfType(ASTMethodDeclaration.class);
                if (method == null) {
                    method = location.getFirstParentOfType(ASTConstructorDeclaration.class);
                    if (method == null) {
                    	method = location.getFirstParentOfType(ASTInitializer.class);
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
                    break;	//Optimization
                }
                
                
            }

            if (violation && !usages.isEmpty()) {
                addViolation(data, node, new Object[] { declaration.getImage() });
            }
        }
        return data;
    }
}
