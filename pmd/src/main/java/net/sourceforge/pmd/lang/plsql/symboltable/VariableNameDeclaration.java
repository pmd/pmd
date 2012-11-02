/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.plsql.symboltable;

import java.util.logging.Logger;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.plsql.ast.ASTFormalParameter;
//import net.sourceforge.pmd.lang.plsql.ast.ASTPrimitiveType;
//import net.sourceforge.pmd.lang.plsql.ast.ASTReferenceType;
import net.sourceforge.pmd.lang.plsql.ast.ASTDatatype;
import net.sourceforge.pmd.lang.plsql.ast.ASTVariableOrConstantDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.AccessNode;
//import net.sourceforge.pmd.lang.plsql.ast.Dimensionable;
//import net.sourceforge.pmd.lang.java.ast.TypeNode;

public class VariableNameDeclaration extends AbstractNameDeclaration {
   private final static Logger LOGGER = Logger.getLogger(VariableNameDeclaration.class.getName()); 

    public VariableNameDeclaration(ASTVariableOrConstantDeclaratorId node) {
	super(node);
    }

    @Override
    public Scope getScope() {
	//SRT return node.getScope().getEnclosingClassScope();
	try {
	  return node.getScope().getEnclosingClassScope();
	}
	catch (Exception e)
	{
	  LOGGER.finest("This Node does not have an enclosing Class: "
		              + node.getBeginLine() + "/" + node.getBeginColumn()
		              + " => " + node.getImage()
		             );
          return null; //SRT a cop-out 
	}
    }

    /* SRT public String getTypeImage() {
	return ((Node) getAccessNodeParent()).jjtGetChild(0).jjtGetChild(0).jjtGetChild(0).getImage();
    }*/

    /**
     * Note that an array of primitive types (int[]) is a reference type.
     */
    //public boolean isReferenceType() {
	//return ((Node) getAccessNodeParent()).jjtGetChild(0).jjtGetChild(0) instanceof ASTDatatype;
    //}


    public AccessNode getAccessNodeParent() {
	if (node.jjtGetParent() instanceof ASTFormalParameter) {
	    return (AccessNode) node.jjtGetParent();
	}
	return (AccessNode) node.jjtGetParent().jjtGetParent();
    }

    public ASTVariableOrConstantDeclaratorId getDeclaratorId() {
	return (ASTVariableOrConstantDeclaratorId) node;
    }

    //public Class<?> getType() {
	//return ((TypeNode) node).getType();
    //}

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof VariableNameDeclaration)) {
            return false;
        }
	VariableNameDeclaration n = (VariableNameDeclaration) o;
	try
	{
	return n.node.getImage().equals(node.getImage());
	}
	catch (Exception e)
	{
		e.printStackTrace(System.err);
		LOGGER.finest("n.node="+n.node);
		LOGGER.finest("n.node.getImage="+n.node.getImage());
		LOGGER.finest("node="+node);
		LOGGER.finest("node.getImage="+node.getImage());
		return false;
	}
    }

    @Override
    public int hashCode() {
	try
	{
	  return node.getImage().hashCode();
	}
	catch(Exception e)
	{
		LOGGER.finest("VariableNameDeclaration: node="
			           +node
			);
		LOGGER.finest("VariableNameDeclaration: node,getImage="
			           +node.getImage()
			);
		//LOGGER.finest("... "
		//	           +" from "+node.getBeginLine() +"@"+node.getBeginColumn()
		//	           +" to "+node.getEndLine() +"@"+node.getEndColumn()
		//	);
		return 0;
	}
    }

    @Override
    public String toString() {
	return "Variable: image = '" + node.getImage() + "', line = " + node.getBeginLine();
    }
}
