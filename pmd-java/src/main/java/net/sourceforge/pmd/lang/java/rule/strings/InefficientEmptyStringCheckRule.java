/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.strings;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.rule.AbstractInefficientZeroCheck;
import net.sourceforge.pmd.lang.java.symboltable.JavaNameOccurrence;

/**
 * This rule finds code which inefficiently determines empty strings. This code
 *
 * <pre>
 *         if(str.trim().length()==0){....
 * </pre>
 *
 * <p>
 * is quite inefficient as trim() causes a new String to be created. Smarter
 * code to check for an empty string would be:
 * </p>
 *
 * <pre>
 * Character.isWhitespace(str.charAt(i));
 * </pre>
 *
 * @author acaplan
 */
public class InefficientEmptyStringCheckRule extends AbstractInefficientZeroCheck {
    
    @Override
    public boolean isTargetMethod(JavaNameOccurrence occ) {
        if (occ.getNameForWhichThisIsAQualifier() != null
                && occ.getNameForWhichThisIsAQualifier().getImage().indexOf("trim") != -1) {
            Node pExpression = occ.getLocation().jjtGetParent().jjtGetParent();
            if (pExpression.jjtGetNumChildren() > 2 && "length".equals(pExpression.jjtGetChild(2).getImage())) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public boolean appliesToClassName(String name) {
        return "String".equals(name);
    }
    
    @Override
    public Object visit(ASTPrimaryExpression node, Object data) {
        
        if (node.jjtGetNumChildren() > 3) {
            // Check last suffix
            if (!"isEmpty".equals(node.jjtGetChild(node.jjtGetNumChildren() - 2).getImage())) {
                return data;
            }
            
            Node prevCall = node.jjtGetChild(node.jjtGetNumChildren() - 4);
            String target = prevCall.jjtGetNumChildren() > 0 ? prevCall.jjtGetChild(0).getImage() : prevCall.getImage();
            if (target != null && target.indexOf("trim") != -1) {
                addViolation(data, node);
            }
        }
        return data;
    }
    
}
