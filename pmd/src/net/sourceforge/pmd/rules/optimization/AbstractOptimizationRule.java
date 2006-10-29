/*
 * Created on Jan 11, 2005 
 *
 * $Id$
 */
package net.sourceforge.pmd.rules.optimization;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.ast.ASTAssignmentOperator;
import net.sourceforge.pmd.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTPostfixExpression;
import net.sourceforge.pmd.ast.ASTPreDecrementExpression;
import net.sourceforge.pmd.ast.ASTPreIncrementExpression;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.ast.Node;
import net.sourceforge.pmd.ast.SimpleNode;

import java.util.Iterator;
import java.util.List;

/**
 * Base class with utility methods for optimization rules
 *
 * @author mgriffa
 */
public class AbstractOptimizationRule extends AbstractRule implements Rule {

    protected final boolean isVarWritterInMethod(String varName, ASTMethodDeclaration md) {
        List assignments = md.findChildrenOfType(ASTAssignmentOperator.class);
        return (variableAssigned(varName, assignments) || numericWithPrePost(md, varName));
    }

    // TODO - symbol table?
    protected final String getVarName(ASTLocalVariableDeclaration node) {
        List l = node.findChildrenOfType(ASTVariableDeclaratorId.class);
        if (!l.isEmpty()) {
            ASTVariableDeclaratorId vd = (ASTVariableDeclaratorId) l.get(0);
            return vd.getImage();
        }
        return null;
    }

    /**
     * Check constructions like
     * int i;
     * ++i;
     * --i;
     * i++;
     * i*=1;
     * i+=1;
     */
    private final boolean numericWithPrePost(ASTMethodDeclaration md, String varName) {
        // ++i
        List preinc = md.findChildrenOfType(ASTPreIncrementExpression.class);
        if (preinc != null && !preinc.isEmpty()) {
            for (Iterator it = preinc.iterator(); it.hasNext();) {
                ASTPreIncrementExpression ie = (ASTPreIncrementExpression) it.next();
                Node prefix = ie.jjtGetChild(0).jjtGetChild(0);
                if (prefix.jjtGetNumChildren() > 0 && ((ASTName) prefix.jjtGetChild(0)).hasImageEqualTo(varName)) {
                    return true;
                }
            }
        }
        
        // --i
        List predec = md.findChildrenOfType(ASTPreDecrementExpression.class);
        if (predec != null && !predec.isEmpty()) {
            for (Iterator it = predec.iterator(); it.hasNext();) {
                ASTPreDecrementExpression de = (ASTPreDecrementExpression) it.next();
                Node prefix = de.jjtGetChild(0).jjtGetChild(0);
                if (prefix.jjtGetNumChildren() > 0 && ((ASTName) prefix.jjtGetChild(0)).hasImageEqualTo(varName)) {
                    return true;
                }
            }
        }

        List pf = md.findChildrenOfType(ASTPostfixExpression.class);
        if (pf != null && !pf.isEmpty()) {
            for (Iterator it = pf.iterator(); it.hasNext();) {
                ASTPostfixExpression pe = (ASTPostfixExpression) it.next();

                if ((pe.hasImageEqualTo("++") || pe.hasImageEqualTo("--"))) {
                    SimpleNode first = (SimpleNode) pe.jjtGetChild(0);
                    SimpleNode second = (SimpleNode) first.jjtGetChild(0);
                    if (second.jjtGetNumChildren() == 0 || !(second.jjtGetChild(0) instanceof ASTName)) {
                        continue;
                    }
                    ASTName name = (ASTName) second.jjtGetChild(0);
                    if (name.hasImageEqualTo(varName)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }


    private final boolean variableAssigned(final String varName, final List assignments) {
        if (assignments == null || assignments.isEmpty()) {
            return false;
        }
        for (Iterator it = assignments.iterator(); it.hasNext();) {
            final ASTAssignmentOperator a = (ASTAssignmentOperator) it.next();
            // if node is assigned return true
            SimpleNode firstChild = (SimpleNode) a.jjtGetParent().jjtGetChild(0);
            SimpleNode otherChild = (SimpleNode) firstChild.jjtGetChild(0);
            if (otherChild.jjtGetNumChildren() == 0 || !(otherChild.jjtGetChild(0) instanceof ASTName)) {
                continue;
            }
            ASTName n = (ASTName) otherChild.jjtGetChild(0);
            if (n.hasImageEqualTo(varName)) {
                return true;
            }
        }

        return false;
    }

}
