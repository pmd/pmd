package net.sourceforge.pmd.lang.java.rule.naming;

import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.java.rule.*;

public class AttributeTypeAndNameIsInconsistentRule extends AbstractJavaRule 
{	
    public Object visit(ASTFieldDeclaration node, Object data) 
    {	
        String nameOfField = node.getVariableName();
        ASTType t = (ASTType) node.jjtGetChild(0); // Is first child always ASTType

        /**************** Type Should Be Boolean As Name Suggests *********************/
        
        if(nameOfField.startsWith("is") && (nameOfField.charAt(2) > 64) && (nameOfField.charAt(2) < 91))  // after is a capital letter expected to not addViolation to a field called isotherm or so

        {
	       	 if(!(t.getType().getName().equals("boolean")))
	       	 {
	       		 addViolation(data, node);
	       	 }
        }
        
        else if((nameOfField.startsWith("has") || nameOfField.startsWith("can")) && 
        		(nameOfField.charAt(3) > 64) && (nameOfField.charAt(3) < 91))
        {
	       	 if(!(t.getType().getName().equals("boolean")))
	       	 {
	       		 addViolation(data, node);
	       	 }
        }
        
        else if((nameOfField.startsWith("have") || nameOfField.startsWith("will")) && 
        		(nameOfField.charAt(4) > 64) && (nameOfField.charAt(4) < 91))
        {
	       	 if(!(t.getType().getName().equals("boolean")))
	       	 {
	       		 addViolation(data, node);
	       	 }
        }
        
        else if(nameOfField.startsWith("should") && 
        		(nameOfField.charAt(6) > 64) && (nameOfField.charAt(6) < 91))
        {
	       	 if(!(t.getType().getName().equals("boolean")))
	       	 {
	       		 addViolation(data, node);
	       	 }
        }
               
        /***************************************************************************/
         		 
        return super.visit(node,data);
    }
    
    
    public Object visit(ASTLocalVariableDeclaration node, Object data) 
    {	
        String nameOfField = node.getVariableName();
        ASTType t = (ASTType) node.jjtGetChild(0); // Is first child always ASTType

        /**************** Type Should Be Boolean As Name Suggests *********************/
        
        if(nameOfField.startsWith("is") && (nameOfField.charAt(2) > 64) && (nameOfField.charAt(2) < 91))  // after is a capital letter expected to not addViolation to a local variable called isotherm or so

        {
	       	 if(!(t.getType().getName().equals("boolean")))
	       	 {
	       		 addViolation(data, node);
	       	 }
        }
        
        else if((nameOfField.startsWith("has") || nameOfField.startsWith("can")) && 
        		(nameOfField.charAt(3) > 64) && (nameOfField.charAt(3) < 91))
        {
	       	 if(!(t.getType().getName().equals("boolean")))
	       	 {
	       		 addViolation(data, node);
	       	 }
        }
        
        else if((nameOfField.startsWith("have") || nameOfField.startsWith("will")) && 
        		(nameOfField.charAt(4) > 64) && (nameOfField.charAt(4) < 91))
        {
	       	 if(!(t.getType().getName().equals("boolean")))
	       	 {
	       		 addViolation(data, node);
	       	 }
        }
        
        else if(nameOfField.startsWith("should") && 
        		(nameOfField.charAt(6) > 64) && (nameOfField.charAt(6) < 91))
        {
	       	 if(!(t.getType().getName().equals("boolean")))
	       	 {
	       		 addViolation(data, node);
	       	 }
        }
               
        /***************************************************************************/
         		 
        return super.visit(node,data);
    }
        
}
