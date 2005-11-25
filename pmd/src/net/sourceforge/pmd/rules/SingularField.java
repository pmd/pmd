/*
 * SingularField.java
 *
 * Created on April 17, 2005, 9:49 PM
 */

package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTConstructorDeclaration;
import net.sourceforge.pmd.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import org.jaxen.JaxenException;

import java.util.List;

/**
 *
 * @author Eric Olander
 */
public class SingularField extends AbstractRule {
    
    public Object visit(ASTFieldDeclaration node, Object data) {
        if (node.isPrivate() && !node.isStatic()) {
            List list = node.findChildrenOfType(ASTVariableDeclaratorId.class);
            ASTVariableDeclaratorId decl = (ASTVariableDeclaratorId)list.get(0);
            String name = decl.getImage();
            String path = "//MethodDeclaration[.//PrimaryExpression[.//Name[@Image = \""+name+"\" or substring-before(@Image, \".\") = \""+name+"\"] or .//PrimarySuffix[@Image = \""+name+"\"]]] |" +
                     "//ConstructorDeclaration[.//PrimaryExpression[.//Name[@Image = \""+name+"\" or substring-before(@Image, \".\") = \""+name+"\"] or .//PrimarySuffix[@Image = \""+name+"\"]]]";
            try {
                List nodes = node.findChildNodesWithXPath(path);
                if (nodes.size() == 1) {
                    String method;
                    if (nodes.get(0) instanceof ASTMethodDeclaration) {
                        method = ((ASTMethodDeclarator)((ASTMethodDeclaration)nodes.get(0)).findChildrenOfType(ASTMethodDeclarator.class).get(0)).getImage();
                    } else {
                        method = ((ASTClassOrInterfaceDeclaration)((ASTConstructorDeclaration)nodes.get(0)).getFirstParentOfType(ASTClassOrInterfaceDeclaration.class)).getImage();
                    }
                    addViolation(data, decl, new Object[]{name, method});
                }
            } catch (JaxenException je) {
                je.printStackTrace();   
            }
        }
        return data;
    }
    
}
