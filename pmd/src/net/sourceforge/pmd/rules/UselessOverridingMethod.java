/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTArgumentList;
import net.sourceforge.pmd.ast.ASTArguments;
import net.sourceforge.pmd.ast.ASTBlock;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceBodyDeclaration;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTFormalParameter;
import net.sourceforge.pmd.ast.ASTFormalParameters;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.ast.ASTStatement;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.ast.Node;
import net.sourceforge.pmd.ast.SimpleNode;

import java.util.ArrayList;
import java.util.List;

public class UselessOverridingMethod extends AbstractRule {

    public Object visit(ASTClassOrInterfaceDeclaration clz, Object data) {
        if (clz.isInterface()) {
            return data;
        }
        return super.visit(clz, data);
    }

    public Object visit(ASTMethodDeclaration node, Object data) {
        // Can skip abstract methods and methods whose only purpose is to
        // guarantee that the inherited method is not changed by finalizing
        // them.
        if (node.isAbstract() || node.isFinal() || node.isNative() || node.isSynchronized()) {
            return super.visit(node, data);
        }

        ASTBlock block = node.getBlock();
        if (block == null) {
            return super.visit(node, data);
        }
        //Only process functions with one BlockStatement
        if (block.jjtGetNumChildren() != 1 || block.findChildrenOfType(ASTStatement.class).size() != 1)
            return super.visit(node, data);

        ASTStatement statement = (ASTStatement) block.jjtGetChild(0).jjtGetChild(0);
        if (statement.jjtGetChild(0).jjtGetNumChildren() == 0) {
            return data;     // skips empty return statements
        }
        SimpleNode statementGrandChild = (SimpleNode) statement.jjtGetChild(0).jjtGetChild(0);
        ASTPrimaryExpression primaryExpression;

        if (statementGrandChild instanceof ASTPrimaryExpression)
            primaryExpression = (ASTPrimaryExpression) statementGrandChild;
        else {
            List primaryExpressions = findFirstDegreeChildrenOfType(statementGrandChild, ASTPrimaryExpression.class);
            if (primaryExpressions.size() != 1)
                return super.visit(node, data);
            primaryExpression = (ASTPrimaryExpression) primaryExpressions.get(0);
        }

        ASTPrimaryPrefix primaryPrefix = (ASTPrimaryPrefix) findFirstDegreeChildrenOfType(primaryExpression, ASTPrimaryPrefix.class).get(0);
        if (!primaryPrefix.usesSuperModifier())
            return super.visit(node, data);

        ASTMethodDeclarator methodDeclarator = (ASTMethodDeclarator) findFirstDegreeChildrenOfType(node, ASTMethodDeclarator.class).get(0);
        if (!primaryPrefix.hasImageEqualTo(methodDeclarator.getImage()))
            return super.visit(node, data);

        //Process arguments
        ASTPrimarySuffix primarySuffix = (ASTPrimarySuffix) findFirstDegreeChildrenOfType(primaryExpression, ASTPrimarySuffix.class).get(0);
        ASTArguments arguments = (ASTArguments) primarySuffix.jjtGetChild(0);
        ASTFormalParameters formalParameters = (ASTFormalParameters) methodDeclarator.jjtGetChild(0);
        if (formalParameters.jjtGetNumChildren() != arguments.jjtGetNumChildren())
            return super.visit(node, data);

        if (arguments.jjtGetNumChildren() == 0) //No arguments to check
            addViolation(data, node, getMessage());
        else {
            ASTArgumentList argumentList = (ASTArgumentList) arguments.jjtGetChild(0);
            for (int i = 0; i < argumentList.jjtGetNumChildren(); i++) {
                Node ExpressionChild = argumentList.jjtGetChild(i).jjtGetChild(0);
                if (!(ExpressionChild instanceof ASTPrimaryExpression) || ExpressionChild.jjtGetNumChildren() != 1)
                    return super.visit(node, data); //The arguments are not simply passed through

                ASTPrimaryExpression argumentPrimaryExpression = (ASTPrimaryExpression) ExpressionChild;
                ASTPrimaryPrefix argumentPrimaryPrefix = (ASTPrimaryPrefix) argumentPrimaryExpression.jjtGetChild(0);
                if (argumentPrimaryPrefix.jjtGetNumChildren() == 0) {
                    return super.visit(node, data); //The arguments are not simply passed through (using "this" for instance)
                }
                Node argumentPrimaryPrefixChild = argumentPrimaryPrefix.jjtGetChild(0);
                if (!(argumentPrimaryPrefixChild instanceof ASTName))
                    return super.visit(node, data); //The arguments are not simply passed through

                if (formalParameters.jjtGetNumChildren() < i + 1) {
                    return super.visit(node, data); // different number of args
                }

                ASTName argumentName = (ASTName) argumentPrimaryPrefixChild;
                ASTFormalParameter formalParameter = (ASTFormalParameter) formalParameters.jjtGetChild(i);
                ASTVariableDeclaratorId variableId = (ASTVariableDeclaratorId) findFirstDegreeChildrenOfType(formalParameter, ASTVariableDeclaratorId.class).get(0);
                if (!argumentName.hasImageEqualTo(variableId.getImage())) {
                    return super.visit(node, data); //The arguments are not simply passed through
                }

            }
            addViolation(data, node, getMessage()); //All arguments are passed through directly
        }
        return super.visit(node, data);
    }

    public List findFirstDegreeChildrenOfType(SimpleNode n, Class targetType) {
        List l = new ArrayList();
        lclFindChildrenOfType(n, targetType, l);
        return l;
    }

    private void lclFindChildrenOfType(Node node, Class targetType, List results) {
        if (node.getClass().equals(targetType)) {
            results.add(node);
        }

        if (node instanceof ASTClassOrInterfaceDeclaration && ((ASTClassOrInterfaceDeclaration) node).isNested()) {
            return;
        }

        if (node instanceof ASTClassOrInterfaceBodyDeclaration && ((ASTClassOrInterfaceBodyDeclaration) node).isAnonymousInnerClass()) {
            return;
        }

        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            Node child = node.jjtGetChild(i);
            if (child.getClass().equals(targetType)) {
                results.add(child);
            }
        }
    }
}
