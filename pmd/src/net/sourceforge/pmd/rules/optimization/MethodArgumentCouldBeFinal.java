/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules.optimization;

import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTFormalParameter;
import net.sourceforge.pmd.ast.ASTInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;

public class MethodArgumentCouldBeFinal extends AbstractOptimizationRule {

    public Object visit(ASTInterfaceDeclaration decl, Object data) {
        return data; // just skip interfaces
    }

    
    /**
     * Find if this variable is ever written.
     * 
     * @see net.sourceforge.pmd.ast.JavaParserVisitor#visit(net.sourceforge.pmd.ast.ASTLocalVariableDeclaration, java.lang.Object)
     */
    public Object visit(ASTMethodDeclaration methodDeclaration, Object data) {

        List l = getNonFinalArguments(methodDeclaration);

        if (l!=null && !l.isEmpty()) {
            //There is at least one non final argument
            for (Iterator it = l.iterator() ; it.hasNext() ; ) {
                final String varName = (String)it.next();
                if (!isVarWritterInMethod(varName, methodDeclaration)) { 
                    addViolation((RuleContext)data, methodDeclaration.getBeginLine());
                }
            }
        }
        
        return data;
    }


    private List getNonFinalArguments(ASTMethodDeclaration methodDeclaration) {
        List fp = methodDeclaration.findChildrenOfType(ASTFormalParameter.class);
        if (fp!=null && !fp.isEmpty()) {
            Vector v = new Vector();
            for (Iterator it = fp.iterator() ; it.hasNext() ; ) {
                ASTFormalParameter p = (ASTFormalParameter) it.next();
                if (!p.isFinal()) {
                    ASTVariableDeclaratorId vd = (ASTVariableDeclaratorId) p.getFirstChildOfType(ASTVariableDeclaratorId.class);
                    v.add(vd.getImage());
                }
            }
            return v;
        }
        return null;
    }

}
