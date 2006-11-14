/*
 * Created on Jan 11, 2005 
 *
 * $Id$
 */
package net.sourceforge.pmd.rules.optimization;

import java.util.Iterator;
import java.util.List;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.Rule;
import net.sourceforge.pmd.ast.ASTAssignmentOperator;
import net.sourceforge.pmd.ast.ASTExpression;
import net.sourceforge.pmd.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTPostfixExpression;
import net.sourceforge.pmd.ast.ASTPreDecrementExpression;
import net.sourceforge.pmd.ast.ASTPreIncrementExpression;
import net.sourceforge.pmd.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.ast.Node;
import net.sourceforge.pmd.ast.SimpleNode;

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
                if (isName((Node)it.next(), varName)) {
                    return true;
                }
            }
        }
        
        // --i
        List predec = md.findChildrenOfType(ASTPreDecrementExpression.class);
        if (predec != null && !predec.isEmpty()) {
            for (Iterator it = predec.iterator(); it.hasNext();) {
                if (isName((Node)it.next(), varName)) {
                    return true;
                }
            }
        }

        List pf = md.findChildrenOfType(ASTPostfixExpression.class);
        if (pf != null && !pf.isEmpty()) {
            for (Iterator it = pf.iterator(); it.hasNext();) {
                ASTPostfixExpression pe = (ASTPostfixExpression) it.next();
                if ((pe.hasImageEqualTo("++") || pe.hasImageEqualTo("--"))) {
                    if (isName(pe, varName)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private final boolean isName(Node node, String varName) {
        Node first = node.jjtGetChild(0);
        Node second = first.jjtGetChild(0);
        if (second.jjtGetNumChildren() == 0) {
            return false;
        }
        
        Node third = second.jjtGetChild(0);
        if (!(third instanceof ASTName)) {
            if (first instanceof ASTPrimaryExpression) {
                // deals with extra parenthesis:
                // "(varName)++" instead of "varName++" for instance
                if (first.jjtGetNumChildren() != 1) {
                    return false;
                }
                if (second instanceof ASTPrimaryPrefix && second.jjtGetNumChildren() == 1 &&
                        third instanceof ASTExpression && third.jjtGetNumChildren() == 1) {
                    return isName(third, varName);
                }    
            }
            return false;
        }
        ASTName name = (ASTName) third;
        return name.hasImageEqualTo(varName);
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
