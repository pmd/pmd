/*
 * Created on Jan 17, 2005 
 *
 * $Id$
 */
package net.sourceforge.pmd.rules.sunsecure;

import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.ast.ASTInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.ast.ASTResultType;
import net.sourceforge.pmd.ast.ASTReturnStatement;
import net.sourceforge.pmd.ast.ASTTypeDeclaration;

import java.util.Iterator;
import java.util.List;

/**
 * Implementation note: this rule currently ignores return types of y.x.z, 
 * currently it handles only local type fields.
 * 
 * @author mgriffa
 */
public class MethodReturnsInternalArray extends AbstractSunSecureRule {

    public Object visit(ASTInterfaceDeclaration decl, Object data) {
        return data; // just skip interfaces
    }

    public Object visit(ASTMethodDeclaration node, Object data) {
        final ASTResultType rt = (ASTResultType) node.getFirstChildOfType(ASTResultType.class);

        if (rt.returnsArray()) {
            final List returns = node.findChildrenOfType(ASTReturnStatement.class);
            
            if (returns!=null) {
                ASTTypeDeclaration td = (ASTTypeDeclaration) node.getFirstParentOfType(ASTTypeDeclaration.class);
                
                for (Iterator it = returns.iterator() ; it.hasNext() ; ) {
                    final ASTReturnStatement ret = (ASTReturnStatement) it.next();
                    final String vn = getReturnedVariableName(ret);
                    if (isField(vn, td)) {
                        if (!isLocalVariable(vn, node)) {  
                            addViolation((RuleContext)data, ret);
                        }  else {
                            // This is to handle field hiding
                            final ASTPrimaryPrefix pp = (ASTPrimaryPrefix) ret.getFirstChildOfType(ASTPrimaryPrefix.class);
                            if (pp!=null && pp.usesThisModifier()) {
                                final ASTPrimarySuffix ps = (ASTPrimarySuffix) ret.getFirstChildOfType(ASTPrimarySuffix.class);
                                if (ps.getImage().equals(vn)) {
                                    addViolation((RuleContext)data, ret);
                                }
                            }
                        }
                    
                    }
                }
            }
        }
        return data;
    }


}
