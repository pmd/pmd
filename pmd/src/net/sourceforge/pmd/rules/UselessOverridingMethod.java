/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTArgumentList;
import net.sourceforge.pmd.ast.ASTArguments;
import net.sourceforge.pmd.ast.ASTBlock;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceBodyDeclaration;
import net.sourceforge.pmd.ast.ASTClassOrInterfaceDeclaration;
import net.sourceforge.pmd.ast.ASTFormalParameter;
import net.sourceforge.pmd.ast.ASTFormalParameters;
import net.sourceforge.pmd.ast.ASTImplementsList;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTNameList;
import net.sourceforge.pmd.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.ast.ASTResultType;
import net.sourceforge.pmd.ast.ASTStatement;
import net.sourceforge.pmd.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.ast.Node;
import net.sourceforge.pmd.ast.SimpleNode;

import org.jaxen.JaxenException;

/**
 * @author Romain Pelisse, bugfix for [ 1522517 ] False +: UselessOverridingMethod
 */
public class UselessOverridingMethod extends AbstractRule {
	private List<String> exceptions;
	private static final String CLONE = "clone";
	private static final String OBJECT = "Object";

	public UselessOverridingMethod()
	{
        exceptions = new ArrayList<String>(1);
        exceptions.add("CloneNotSupportedException");
	}

	public Object visit(ASTImplementsList clz, Object data)
	{
		return super.visit(clz,data);
	}

    public Object visit(ASTClassOrInterfaceDeclaration clz, Object data) {
        if (clz.isInterface()) {
            return data;
        }
        return super.visit(clz, data);
    }

    //TODO: this method should be externalize into an utility class, shouldn't it ?
    private boolean isMethodType(ASTMethodDeclaration node,String methodType)
    {
    	boolean result = false;
    	ASTResultType type = node.getResultType();
    	if ( type != null ) {
    		List results = null;
            try {
	            results = type.findChildNodesWithXPath("./Type/ReferenceType/ClassOrInterfaceType[@Image = '" + methodType + "']");
            }
            catch (JaxenException e) {
	            e.printStackTrace();
            }
    		if ( results != null && results.size() > 0 ) {
    			result = true;
    		}
    	}
    	return result;
    }

    //TODO: this method should be externalize into an utility class, shouldn't it ?
    private boolean isMethodThrowingType(ASTMethodDeclaration node, List<String> exceptedExceptions) {
    	boolean result = false;
	    ASTNameList thrownsExceptions = node.getFirstChildOfType(ASTNameList.class);
	    if ( thrownsExceptions != null ) {
	    	List<ASTName> names = thrownsExceptions.findChildrenOfType(ASTName.class);
	    	for ( ASTName name : names ) {
	    		for ( String exceptedException : exceptedExceptions) {
		    		if ( exceptedException.equals(name.getImage()) )
		    			result = true;
	    		}
	    	}
	    }
	    return result;
    }

	private boolean hasArguments(ASTMethodDeclaration node) {
		boolean result = false;
		try
		{
			List parameters = node.findChildNodesWithXPath("./MethodDeclarator/FormalParameters/*");
			if ( parameters != null && parameters.size() < 0 ) {
				result = true;
			}
		} catch (JaxenException e) {
			e.printStackTrace();
		}
		return result;
	}

    public Object visit(ASTMethodDeclaration node, Object data) {
        // Can skip abstract methods and methods whose only purpose is to
        // guarantee that the inherited method is not changed by finalizing
        // them.
        if (node.isAbstract() || node.isFinal() || node.isNative() || node.isSynchronized()) {
            return super.visit(node, data);
        }
        // We can also skip the 'clone' method as they are generally
        // 'useless' but as it is considered a 'good practise' to
        // implement them anyway ( see bug 1522517)
        if ( CLONE.equals(node.getMethodName()) && node.isPublic() &&
        	 ! this.hasArguments(node) &&
        	 this.isMethodType(node, OBJECT) &&
        	 this.isMethodThrowingType(node,exceptions) )
        {
        	return super.visit(node,data);
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
            List<ASTPrimaryExpression> primaryExpressions = findFirstDegreeChildrenOfType(statementGrandChild, ASTPrimaryExpression.class);
            if (primaryExpressions.size() != 1)
                return super.visit(node, data);
            primaryExpression = primaryExpressions.get(0);
        }

        ASTPrimaryPrefix primaryPrefix = findFirstDegreeChildrenOfType(primaryExpression, ASTPrimaryPrefix.class).get(0);
        if (!primaryPrefix.usesSuperModifier())
            return super.visit(node, data);

        ASTMethodDeclarator methodDeclarator = findFirstDegreeChildrenOfType(node, ASTMethodDeclarator.class).get(0);
        if (!primaryPrefix.hasImageEqualTo(methodDeclarator.getImage()))
            return super.visit(node, data);

        //Process arguments
        ASTPrimarySuffix primarySuffix = findFirstDegreeChildrenOfType(primaryExpression, ASTPrimarySuffix.class).get(0);
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
                ASTVariableDeclaratorId variableId = findFirstDegreeChildrenOfType(formalParameter, ASTVariableDeclaratorId.class).get(0);
                if (!argumentName.hasImageEqualTo(variableId.getImage())) {
                    return super.visit(node, data); //The arguments are not simply passed through
                }

            }
            addViolation(data, node, getMessage()); //All arguments are passed through directly
        }
        return super.visit(node, data);
    }



	public <T> List<T> findFirstDegreeChildrenOfType(SimpleNode n, Class<T> targetType) {
        List<T> l = new ArrayList<T>();
        lclFindChildrenOfType(n, targetType, l);
        return l;
    }

    private <T> void lclFindChildrenOfType(Node node, Class<T> targetType, List<T> results) {
        if (node.getClass().equals(targetType)) {
            results.add((T) node);
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
                results.add((T) child);
            }
        }
    }
}
