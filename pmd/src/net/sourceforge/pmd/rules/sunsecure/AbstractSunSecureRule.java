/*
 * Created on Jan 17, 2005 
 *
 * $Id$
 */
package net.sourceforge.pmd.rules.sunsecure;

import java.util.Iterator;
import java.util.List;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.ast.ASTResultType;
import net.sourceforge.pmd.ast.ASTReturnStatement;
import net.sourceforge.pmd.ast.ASTType;
import net.sourceforge.pmd.ast.ASTTypeDeclaration;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.ast.SimpleNode;

/**
 * Utility methods for the package
 *  
 * @author mgriffa
 */
public abstract class AbstractSunSecureRule extends AbstractRule {

    /**
     * Tells if the type declaration has a field with varName.
     * 
     * @param varName the name of the field to search
     * @param typeDeclaration the type declaration
     * @return <code>true</code> if there is a field in the type declaration named varName, <code>false</code> in other case
     */
    protected final boolean isField(String varName, ASTTypeDeclaration typeDeclaration) {
       final List fds = typeDeclaration.findChildrenOfType(ASTFieldDeclaration.class);
       if (fds!=null) {
           for (Iterator it=fds.iterator() ; it.hasNext(); ) {
               final ASTFieldDeclaration fd = (ASTFieldDeclaration) it.next();
               final ASTVariableDeclaratorId vid = (ASTVariableDeclaratorId) fd.getFirstChildOfType(ASTVariableDeclaratorId.class);
               if (vid!=null && vid.getImage().equals(varName)) {
                   return true;
               }
           }
       }
       return false;
    }
    

    /**
     * Gets the name of the variable returned.
     * Some examples: <br>
     * for this.foo returns foo <br>
     * for foo returns foo <br>
     * for foo.bar returns foo.bar 
     * 
     * @param ret a return statement to evaluate
     * @return the name of the variable associated or <code>null</code> if it cannot be detected
     */
    protected final String getReturnedVariableName(ASTReturnStatement ret) {
        final ASTName n = (ASTName) ret.getFirstChildOfType(ASTName.class);
        if (n!=null)
            return n.getImage();
        final ASTPrimarySuffix ps = (ASTPrimarySuffix) ret.getFirstChildOfType(ASTPrimarySuffix.class);
        if (ps!=null)
            return ps.getImage();
        return null;
    }
    
    /**
     * Tells if the variable name is a local variable declared in the method.
     * @param vn the variable name
     * @param node the ASTMethodDeclaration where the local variable name will be searched
     * @return <code>true</code> if the method declaration contains any local variable named vn and <code>false</code> in other case
     */
    protected boolean isLocalVariable(String vn, ASTMethodDeclaration node) {
        final List lvars = node.findChildrenOfType(ASTLocalVariableDeclaration.class);
        if (lvars!=null) {
            for (Iterator it = lvars.iterator() ; it.hasNext() ; ) {
                final ASTLocalVariableDeclaration lvd = (ASTLocalVariableDeclaration) it.next();
                final ASTVariableDeclaratorId vid = (ASTVariableDeclaratorId) lvd.getFirstChildOfType(ASTVariableDeclaratorId.class);
                if (vid!=null && vid.getImage().equals(vn)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Gets the image of the first ASTName node found by {@link SimpleNode#getFirstChildOfType(Class)}
     * 
     * @param n the node to search 
     * @return the image of the first ASTName or <code>null</code>
     */
    protected String getFirstNameImage(SimpleNode n) {
        ASTName name = (ASTName) n.getFirstChildOfType(ASTName.class);
        if (name!=null)
            return name.getImage();
        return null;
    }

}
