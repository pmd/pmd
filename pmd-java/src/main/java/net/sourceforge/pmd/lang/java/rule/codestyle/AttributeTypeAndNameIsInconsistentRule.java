package net.sourceforge.pmd.lang.java.rule.codestyle;

import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTType;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class AttributeTypeAndNameIsInconsistentRule extends AbstractJavaRule
{
    @Override
    public Object visit(ASTFieldDeclaration node, Object data)
    {
        String nameOfField = node.getVariableName();
        ASTType t = node.getFirstChildOfType(ASTType.class);

        /**************** Type Should Be Boolean As Name Suggests *********************/

        if(nameOfField.startsWith("is") &&  nameOfField.length() > 2 && (nameOfField.charAt(2) > 64) && (nameOfField.charAt(2) < 91))  // after is a capital letter expected to not addViolation to a field called isotherm or so

        {
	       	 if(t != null & !(t.getType().getName().equals("boolean")))
	       	 {
	       		 addViolation(data, node);
	       	 }
        }

        else if((nameOfField.startsWith("has") || nameOfField.startsWith("can")) && nameOfField.length() > 3 &&
        		(nameOfField.charAt(3) > 64) && (nameOfField.charAt(3) < 91))
        {
	       	 if(t != null & !(t.getType().getName().equals("boolean")))
	       	 {
	       		 addViolation(data, node);
	       	 }
        }

        else if((nameOfField.startsWith("have") || nameOfField.startsWith("will")) && nameOfField.length() > 4 &&
        		(nameOfField.charAt(4) > 64) && (nameOfField.charAt(4) < 91))
        {
	       	 if(t != null & !(t.getType().getName().equals("boolean")))
	       	 {
	       		 addViolation(data, node);
	       	 }
        }

        else if(nameOfField.startsWith("should") && nameOfField.length() > 6 &&
        		(nameOfField.charAt(6) > 64) && (nameOfField.charAt(6) < 91))
        {
	       	 if(t != null & !(t.getType().getName().equals("boolean")))
	       	 {
	       		 addViolation(data, node);
	       	 }
        }

        /***************************************************************************/

        return super.visit(node,data);
    }


    @Override
    public Object visit(ASTLocalVariableDeclaration node, Object data)
    {
        String nameOfField = node.getVariableName();
        ASTType t = node.getFirstChildOfType(ASTType.class);

        /**************** Type Should Be Boolean As Name Suggests *********************/

        if(nameOfField.startsWith("is") && nameOfField.length() > 2 && (nameOfField.charAt(2) > 64) && (nameOfField.charAt(2) < 91))  // after is a capital letter expected to not addViolation to a local variable called isotherm or so

        {
	       	 if(t != null & !(t.getType().getName().equals("boolean")))
	       	 {
	       		 addViolation(data, node);
	       	 }
        }

        else if((nameOfField.startsWith("has") || nameOfField.startsWith("can")) && nameOfField.length() > 3 &&
        		(nameOfField.charAt(3) > 64) && (nameOfField.charAt(3) < 91))
        {
	       	 if(t != null & !(t.getType().getName().equals("boolean")))
	       	 {
	       		 addViolation(data, node);
	       	 }
        }

        else if((nameOfField.startsWith("have") || nameOfField.startsWith("will")) && nameOfField.length() > 4 &&
        		(nameOfField.charAt(4) > 64) && (nameOfField.charAt(4) < 91))
        {
	       	 if(t != null & !(t.getType().getName().equals("boolean")))
	       	 {
	       		 addViolation(data, node);
	       	 }
        }

        else if(nameOfField.startsWith("should") && nameOfField.length() > 6 &&
        		(nameOfField.charAt(6) > 64) && (nameOfField.charAt(6) < 91))
        {
	       	 if(t != null & !(t.getType().getName().equals("boolean")))
	       	 {
	       		 addViolation(data, node);
	       	 }
        }

        /***************************************************************************/

        return super.visit(node,data);
    }

}
