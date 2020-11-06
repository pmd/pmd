/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.errorprone;

import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAllocationExpression;
import net.sourceforge.pmd.lang.java.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class SingletonClassReturningNewInstanceRule extends AbstractJavaRule {

    @Override
    public Object visit(ASTMethodDeclaration node, Object data) {

        boolean violation = false;
        String localVarName = null;
        String returnVariableName = null;

        if (node.getResultType().isVoid()) {
            return super.visit(node, data);
        }

        if ("getInstance".equals(node.getName())) {
            List<ASTReturnStatement> rsl = node.findDescendantsOfType(ASTReturnStatement.class);
            if (rsl.isEmpty()) {
                return super.visit(node, data);
            } else {
                for (ASTReturnStatement rs : rsl) {

                    List<ASTPrimaryExpression> pel = rs.findDescendantsOfType(ASTPrimaryExpression.class);
                    ASTPrimaryExpression ape = pel.get(0);
                    if (ape.getFirstDescendantOfType(ASTAllocationExpression.class) != null) {
                        violation = true;
                        break;
                    }
                }
            }

            /*
             * public class Singleton {
             *
             * private static Singleton m_instance=null;
             *
             * public static Singleton getInstance() {
             *
             * Singleton m_instance=null;
             *
             * if ( m_instance == null ) { synchronized(Singleton.class) {
             * if(m_instance == null) { m_instance = new Singleton(); } } }
             * return m_instance; } }
             */

            List<ASTBlockStatement> astBlockStatements = node.findDescendantsOfType(ASTBlockStatement.class);
            returnVariableName = getReturnVariableName(node);
            if (!astBlockStatements.isEmpty()) {
                for (ASTBlockStatement blockStatement : astBlockStatements) {
                    if (blockStatement.hasDescendantOfType(ASTLocalVariableDeclaration.class)) {
                        List<ASTLocalVariableDeclaration> lVarList = blockStatement
                                .findDescendantsOfType(ASTLocalVariableDeclaration.class);
                        if (!lVarList.isEmpty()) {
                            for (ASTLocalVariableDeclaration localVar : lVarList) {
                                for (ASTVariableDeclaratorId id : localVar) {
                                    localVarName = id.getVariableName();
                                    if (returnVariableName != null && returnVariableName.equals(localVarName)) {
                                        violation = true;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (violation) {
            addViolation(data, node);
        }
        return super.visit(node, data);
    }

    private String getReturnVariableName(ASTMethodDeclaration node) {

        List<ASTReturnStatement> rsl = node.findDescendantsOfType(ASTReturnStatement.class);
        ASTReturnStatement rs = rsl.get(0);
        List<ASTPrimaryExpression> pel = rs.findDescendantsOfType(ASTPrimaryExpression.class);
        ASTPrimaryExpression ape = pel.get(0);
        Node lastChild = ape.getChild(0);
        String returnVariableName = null;
        if (lastChild instanceof ASTPrimaryPrefix) {
            returnVariableName = getNameFromPrimaryPrefix((ASTPrimaryPrefix) lastChild);
        }
        /*
         * if(lastChild instanceof ASTPrimarySuffix){ returnVariableName =
         * getNameFromPrimarySuffix((ASTPrimarySuffix) lastChild); }
         */
        return returnVariableName;

    }

    private String getNameFromPrimaryPrefix(ASTPrimaryPrefix pp) {
        if (pp.getNumChildren() == 1 && pp.getChild(0) instanceof ASTName) {
            return ((ASTName) pp.getChild(0)).getImage();
        }
        return null;
    }
}
