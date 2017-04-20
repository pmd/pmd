import net.sourceforge.pmd.lang.java.ast.*;
import net.sourceforge.pmd.lang.java.rule.*;

public class IsDoesNotReturnBooleanRule extends AbstractJavaRule 
{
    public Object visit(ASTMethodDeclaration node, Object data) 
    {	
        String nameOfMethod = node.getMethodName();
        ASTType a = (ASTType) node.getResultType().jjtGetChild(0); // Is first child always ASTType
        
        if(nameOfMethod.startsWith("is") && (nameOfMethod.charAt(2) > 64) && (nameOfMethod.charAt(2) < 91)) // after is a capital letter expected to not addViolation to a method called isotherm or so
        {
        	 if(!(a.getType().getName().equals("boolean")))
        	 {
        		 addViolation(data, node);
        	 }
        }
        return super.visit(node,data);
    }
}
