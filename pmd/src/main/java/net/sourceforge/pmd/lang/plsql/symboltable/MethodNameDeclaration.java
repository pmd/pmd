/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.plsql.symboltable;

import java.util.logging.Logger;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.plsql.ast.ASTFormalParameter;
import net.sourceforge.pmd.lang.plsql.ast.ASTFormalParameters;
import net.sourceforge.pmd.lang.plsql.ast.ASTMethodDeclarator;
import net.sourceforge.pmd.lang.plsql.ast.ASTTriggerTimingPointSection;
import net.sourceforge.pmd.lang.plsql.ast.SimpleNode;
//import net.sourceforge.pmd.lang.plsql.ast.ASTPrimitiveType;

public class MethodNameDeclaration extends AbstractNameDeclaration {
   private final static Logger LOGGER = Logger.getLogger(MethodNameDeclaration.class.getName()); 

    public MethodNameDeclaration(ASTMethodDeclarator node) {
        super(node);
    }

    /** Treat a TimingPointSection within a Compound Trigger like a 
     *  packaged FUNCTION or PROCEDURE.
     *  SRT 
     * 
     * @param node 
     */
    public MethodNameDeclaration(ASTTriggerTimingPointSection node) {
        super(node);
    }

    public int getParameterCount() {
        return ((ASTMethodDeclarator) node).getParameterCount();
    }

    public boolean isVarargs() {
        ASTFormalParameters params = (ASTFormalParameters) node.jjtGetChild(0);
        for (int i = 0; i < ((ASTMethodDeclarator) node).getParameterCount(); i++) {
            ASTFormalParameter p = (ASTFormalParameter) params.jjtGetChild(i);
            //if (p.isVarargs()) {
            //	return true;
            //}
        }
        return false;
    }

    public ASTMethodDeclarator getMethodNameDeclaratorNode() {
        return (ASTMethodDeclarator) node;
    }

    public String getParameterDisplaySignature() {
    	StringBuilder sb = new StringBuilder("(");
        ASTFormalParameters params = (ASTFormalParameters) node.jjtGetChild(0);
        // TODO - this can be optimized - add [0] then ,[n] in a loop.
        //        no need to trim at the end
        for (int i = 0; i < ((ASTMethodDeclarator) node).getParameterCount(); i++) {
            ASTFormalParameter p = (ASTFormalParameter) params.jjtGetChild(i);
            sb.append(p.getTypeNode().getTypeImage());
            //if (p.isVarargs()) {
            //	sb.append("...");
            //}
            sb.append(',');
        }
        if (sb.charAt(sb.length() - 1) == ',') {
            sb.deleteCharAt(sb.length() - 1);
        }
        sb.append(')');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof MethodNameDeclaration)) {
            return false;
        }

        MethodNameDeclaration other = (MethodNameDeclaration) o;

        // compare name
        if (!other.node.getCanonicalImage().equals(node.getCanonicalImage())) {
            return false;
        }

        // compare parameter count - this catches the case where there are no params, too
        if (((ASTMethodDeclarator) other.node).getParameterCount() != ((ASTMethodDeclarator) node).getParameterCount()) {
            return false;
        }

        // compare parameter types
        //SRT ASTFormalParameters myParams = (ASTFormalParameters) node.jjtGetChild(0);
        //SRT ASTFormalParameters otherParams = (ASTFormalParameters) other.node.jjtGetChild(0);
        ASTFormalParameters myParams = node.getFirstDescendantOfType(ASTFormalParameters.class) ;
        ASTFormalParameters otherParams = other.node.getFirstDescendantOfType(ASTFormalParameters.class) ;
        for (int i = 0; i < ((ASTMethodDeclarator) node).getParameterCount(); i++) {
            ASTFormalParameter myParam = (ASTFormalParameter) myParams.jjtGetChild(i);
            ASTFormalParameter otherParam = (ASTFormalParameter) otherParams.jjtGetChild(i);

            // Compare vararg
            //if (myParam.isVarargs() != otherParam.isVarargs()) {
            //	return false;
            //}

            Node myTypeNode = myParam.getTypeNode().jjtGetChild(0);
            Node otherTypeNode = otherParam.getTypeNode().jjtGetChild(0);

            // compare primitive vs reference type
            if (myTypeNode.getClass() != otherTypeNode.getClass()) {
                return false;
            }

            // simple comparison of type images
            // this can be fooled by one method using "String"
            // and the other method using "java.lang.String"
            // once we get real types in here that should get fixed
            String myTypeImg;
            String otherTypeImg;
            //if (myTypeNode instanceof ASTPrimitiveType) {
            //    myTypeImg = myTypeNode.getCanonicalImage();
            //    otherTypeImg = otherTypeNode.getCanonicalImage();
            //} else {
                myTypeImg = ( (SimpleNode) myTypeNode .jjtGetChild(0) ) .getCanonicalImage();
                otherTypeImg = ( (SimpleNode) otherTypeNode.jjtGetChild(0) ).getCanonicalImage();
            //}

            if (!myTypeImg.equals(otherTypeImg)) {
                return false;
            }

            // if type is ASTPrimitiveType and is an array, make sure the other one is also
        }
        return true;
    }

    @Override
    public int hashCode() {
	try 
	{
        return node.hashCode(); //SRT node.getCanonicalImage().hashCode() + ((ASTMethodDeclarator) node).getParameterCount();
	}
	catch (Exception e)
	{
	  LOGGER.finest("MethodNameDeclaration problem for " + node
			 +" of class " + node.getClass().getCanonicalName()
		         +" => "+ node.getBeginLine()+"/"+node.getBeginColumn()
			);	
	  //@TODO SRT restore the thrown exception - throw e;
	  return 0; 
	}
    }

    @Override
    public String toString() {
        //SRT return "Method " + node.getCanonicalImage() + ", line " + node.getBeginLine() + ", params = " + ((ASTMethodDeclarator) node).getParameterCount();
        return node.toString();
    }
}
