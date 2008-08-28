/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.strings;

import java.util.Iterator;
import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAdditiveExpression;
import net.sourceforge.pmd.lang.java.ast.ASTAllocationExpression;
import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTStatementExpression;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.symboltable.VariableNameDeclaration;
import net.sourceforge.pmd.lang.java.typeresolution.TypeHelper;

/*
 * How this rule works:
 * find additive expressions: +
 * check that the addition is between anything other than two literals
 * if true and also the parent is StringBuffer constructor or append,
 * report a violation.
 *
 * @author mgriffa
 */
public class InefficientStringBufferingRule extends AbstractJavaRule {

    @Override
    public Object visit(ASTAdditiveExpression node, Object data) {
        ASTBlockStatement bs = node.getFirstParentOfType(ASTBlockStatement.class);
        if (bs == null) {
            return data;
        }

        int immediateLiterals = 0;
        List<ASTLiteral> nodes = node.findDescendantsOfType(ASTLiteral.class);
        for (ASTLiteral literal: nodes) {
            if (literal.getNthParent(3) == node) {
                immediateLiterals++;
            }
            if (!literal.isStringLiteral()) {
                return data;
            }
        }

        if (immediateLiterals > 1) {
            return data;
        }

        // if literal + public static final, return
        List<ASTName> nameNodes = node.findDescendantsOfType(ASTName.class);
        for (ASTName name: nameNodes) {
            if (name.getNameDeclaration() instanceof VariableNameDeclaration) {
                VariableNameDeclaration vnd = (VariableNameDeclaration)name.getNameDeclaration();
                if (vnd.getAccessNodeParent().isFinal() && vnd.getAccessNodeParent().isStatic()) {
                    return data;
                }
            }
        }

        if (bs.isAllocation()) {
            for (Iterator<ASTName> iterator = nameNodes.iterator(); iterator.hasNext();) {
            	ASTName name = iterator.next();
    			if (!name.getImage().endsWith("length")) {
    				break;
    			} else if (!iterator.hasNext()) {
    				return data;	//All names end with length
    			}
    		}

            if (isAllocatedStringBuffer(node)) {
                addViolation(data, node);
            }
        } else if (isInStringBufferOperation(node, 6, "append")) {
            addViolation(data, node);
        }
        return data;
    }

    protected static boolean isInStringBufferOperation(Node node, int length, String methodName) {
        if (!(node.getNthParent(length) instanceof ASTStatementExpression)) {
            return false;
        }
        ASTStatementExpression s = node.getFirstParentOfType(ASTStatementExpression.class);
        if (s == null) {
            return false;
        }
        ASTName n = s.getFirstDescendantOfType(ASTName.class);
        if (n == null || n.getImage().indexOf(methodName) == -1 || !(n.getNameDeclaration() instanceof VariableNameDeclaration)) {
            return false;
        }

        // TODO having to hand-code this kind of dredging around is ridiculous
        // we need something to support this in the framework
        // but, "for now" (tm):
        // if more than one arg to append(), skip it
        ASTArgumentList argList = s.getFirstDescendantOfType(ASTArgumentList.class);
        if (argList == null || argList.jjtGetNumChildren() > 1) {
            return false;
        }
        return TypeHelper.isA((VariableNameDeclaration)n.getNameDeclaration(), StringBuffer.class);
    }

    private boolean isAllocatedStringBuffer(ASTAdditiveExpression node) {
        ASTAllocationExpression ao = node.getFirstParentOfType(ASTAllocationExpression.class);
        if (ao == null) {
            return false;
        }
        // note that the child can be an ArrayDimsAndInits, for example, from java.lang.FloatingDecimal:  t = new int[ nWords+wordcount+1 ];
        ASTClassOrInterfaceType an = ao.getFirstChildOfType(ASTClassOrInterfaceType.class);
        return an != null && (TypeHelper.isA(an, StringBuffer.class) || TypeHelper.isA(an, StringBuilder.class));
    }
}

