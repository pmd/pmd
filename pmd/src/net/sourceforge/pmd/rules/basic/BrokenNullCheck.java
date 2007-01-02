package net.sourceforge.pmd.rules.basic;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTConditionalAndExpression;
import net.sourceforge.pmd.ast.ASTConditionalOrExpression;
import net.sourceforge.pmd.ast.ASTEqualityExpression;
import net.sourceforge.pmd.ast.ASTExpression;
import net.sourceforge.pmd.ast.ASTIfStatement;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTNullLiteral;
import net.sourceforge.pmd.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.ast.Node;
import net.sourceforge.pmd.ast.SimpleJavaNode;

public class BrokenNullCheck extends AbstractRule {

    public Object visit(ASTIfStatement node, Object data) {
        ASTExpression expression = (ASTExpression)node.jjtGetChild(0);
        
        ASTConditionalAndExpression conditionalAndExpression = (ASTConditionalAndExpression)expression.getFirstChildOfType(ASTConditionalAndExpression.class);
        if (conditionalAndExpression != null) {
            checkForViolations(node, data, conditionalAndExpression);
        }
        
        ASTConditionalOrExpression conditionalOrExpression = (ASTConditionalOrExpression)expression.getFirstChildOfType(ASTConditionalOrExpression.class);
        if (conditionalOrExpression != null) {
            checkForViolations(node, data, conditionalOrExpression);
        }

        return super.visit(node, data);
    }


    private void checkForViolations(ASTIfStatement node, Object data, SimpleJavaNode conditionalExpression) {
        ASTEqualityExpression equalityExpression = (ASTEqualityExpression)getFirstDirectChildOfType(ASTEqualityExpression.class, conditionalExpression);
        if (equalityExpression == null) {
            return;
        }
        if (conditionalExpression instanceof ASTConditionalAndExpression && 
                !"==".equals(equalityExpression.getImage())) {
            return;
        }
        if (conditionalExpression instanceof ASTConditionalOrExpression && 
                !"!=".equals(equalityExpression.getImage())) {
            return;
        }
        ASTNullLiteral nullLiteral = (ASTNullLiteral)equalityExpression.getFirstChildOfType(ASTNullLiteral.class);
        if (nullLiteral == null) {
            return;     //No null check
        }

        //Find the expression used in the null compare
        ASTPrimaryExpression nullCompareExpression = findNullCompareExpression(equalityExpression);
        if (nullCompareExpression == null) {
            return;     //No good null check
        }
        
        //Now we find the expression to compare to and do the comparison
        for (int i = 0; i < conditionalExpression.jjtGetNumChildren(); i++) {
            SimpleJavaNode conditionalSubnode = (SimpleJavaNode)conditionalExpression.jjtGetChild(i);
            //We skip the ASTEqualityExpression, which is the null compare branch
            if (!(conditionalSubnode instanceof ASTEqualityExpression)) {
                if (!(conditionalSubnode instanceof ASTPrimaryExpression)) {
                    //The ASTPrimaryExpression is hidden (probably in a negation)
                    conditionalSubnode = (SimpleJavaNode)conditionalSubnode.getFirstChildOfType(ASTPrimaryExpression.class);
                }
            }
            if (conditionalSubnode instanceof ASTPrimaryExpression) {
                if (primaryExpressionsAreEqual(nullCompareExpression, (ASTPrimaryExpression)conditionalSubnode)) {
                    addViolation(data, node);   //We have a match
                }
            }
            
        }
    }

    private boolean primaryExpressionsAreEqual(ASTPrimaryExpression nullCompareVariable, ASTPrimaryExpression expressionUsage) {
        List nullCompareNames = new ArrayList();
        findVariableNames(nullCompareVariable, nullCompareNames);
        
        List expressionUsageNames = new ArrayList();
        findVariableNames(expressionUsage, expressionUsageNames);
        
        for (int i = 0; i < nullCompareNames.size(); i++) {
            if (expressionUsageNames.size() == i) {
                return false;   //The used expression is shorter than the null compare expression
            }
            
            String nullCompareExpressionName = (String)nullCompareNames.get(i);
            String expressionUsageName       = (String)expressionUsageNames.get(i);
            
            //Variablenames should match or the expressionUsage should have the variable with a method call (ie. var.equals())
            if (!nullCompareExpressionName.equals(expressionUsageName) &&
                    !expressionUsageName.startsWith(nullCompareExpressionName + ".")) {
                return false;   //Some other expression is being used after the null compare
            }
        }

        return true;
    }


    /**
     * Find the variable names in a PrimaryExpression (thus in the Name and PrimarySuffix children).
     */
    private void findVariableNames(Node nullCompareVariable, List results) {
        for (int i = 0; i < nullCompareVariable.jjtGetNumChildren(); i++) {
            Node child = nullCompareVariable.jjtGetChild(i);
            
            if (child.getClass().equals(ASTName.class)) {
                results.add( ((ASTName)child).getImage() );
            } else if (child.getClass().equals(ASTPrimarySuffix.class)) {
                String name = ((ASTPrimarySuffix)child).getImage();
                if (name != null && !name.equals("")) {
                    results.add(name);
                }
            }
            
            if (child.jjtGetNumChildren() > 0) {
                findVariableNames(child, results);
            }
        }
    }


    private ASTPrimaryExpression findNullCompareExpression(ASTEqualityExpression equalityExpression) {
        List primaryExpressions = equalityExpression.findChildrenOfType(ASTPrimaryExpression.class);
        for (Iterator iter = primaryExpressions.iterator(); iter.hasNext();) {
            ASTPrimaryExpression primaryExpression = (ASTPrimaryExpression) iter.next();
            List primaryPrefixes = primaryExpression.findChildrenOfType(ASTPrimaryPrefix.class);
            for (Iterator iterator = primaryPrefixes.iterator(); iterator.hasNext();) {
                ASTPrimaryPrefix primaryPrefix = (ASTPrimaryPrefix) iterator.next();
                ASTName name = (ASTName)primaryPrefix.getFirstChildOfType(ASTName.class);
                if (name != null) {
                    //We found the variable that is compared to null
                    return primaryExpression;
                }
            }
        }
        return null;  //Nothing found
    }

    private Node getFirstDirectChildOfType(Class childType, Node node) {
        for (int i = 0; i < node.jjtGetNumChildren(); i++) {
            SimpleJavaNode simpleNode = (SimpleJavaNode) node.jjtGetChild(i);
            if (simpleNode.getClass().equals(childType))
                return simpleNode;
        }
        return null;
    }
}
