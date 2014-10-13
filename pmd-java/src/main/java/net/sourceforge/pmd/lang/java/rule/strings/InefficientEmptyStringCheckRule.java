/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.rule.strings;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.rule.AbstractInefficientZeroCheck;
import net.sourceforge.pmd.lang.java.symboltable.JavaNameOccurrence;

/**
 * This rule finds code which inefficiently determines empty strings. This code
 * <p/>
 * 
 * <pre>
 *         if(str.trim().length()==0){....
 * </pre>
 * 
 * <p/> is quite inefficient as trim() causes a new String to be created.
 * Smarter code to check for an empty string would be: <p/>
 * 
 * <pre>
 * Character.isWhitespace(str.charAt(i));
 * </pre>
 * 
 * @author acaplan
 */
public class InefficientEmptyStringCheckRule extends AbstractInefficientZeroCheck {

    /**
     * Determine if we're dealing with String.length method
     * 
     * @param occ
     *            The name occurrence
     * @return true if it's String.length, else false
     */
    public boolean isTargetMethod(JavaNameOccurrence occ) {
        if (occ.getNameForWhichThisIsAQualifier() != null
                && occ.getNameForWhichThisIsAQualifier().getImage().indexOf("trim") != -1) {
            Node pExpression = occ.getLocation().jjtGetParent().jjtGetParent();
            if (pExpression.jjtGetNumChildren() >= 3
                    && "length".equals(pExpression.jjtGetChild(2).getImage())) {
                return true;
            }
        }
        return false;
    }

    public boolean appliesToClassName(String name) {
        return "String".equals(name);
    }

}