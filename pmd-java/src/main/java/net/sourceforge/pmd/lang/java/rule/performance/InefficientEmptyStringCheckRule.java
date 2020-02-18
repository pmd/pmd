/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.performance;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.rule.AbstractInefficientZeroCheck;
import net.sourceforge.pmd.lang.java.symboltable.JavaNameOccurrence;

/**
 * This rule finds code which inefficiently determines empty strings.
 *
 * <p>
 * <pre>
 * str.trim().length()==0
 * </pre>
 * or
 * <pre>
 * str.trim().isEmpty()
 * </pre>
 * (for the same reason) is quite inefficient as trim() causes a new String to
 * be created. A Smarter code to check for an empty string would be:
 *
 * <pre>
 * private boolean checkTrimEmpty(String str) {
 *     for(int i = 0; i &lt; str.length(); i++) {
 *         if(!Character.isWhitespace(str.charAt(i))) {
 *             return false;
 *         }
 *     }
 *     return true;
 * }
 * </pre>
 * or you can refer to Apache's <code>StringUtils#isBlank</code>
 * (in commons-lang), Spring's <code>StringUtils#hasText</code> (in the Spring
 * framework) or Google's <code>CharMatcher#whitespace</code> (in Guava) for
 * existing implementations (some might include the check for != null).
 * </p>
 *
 * @author acaplan
 */
public class InefficientEmptyStringCheckRule extends AbstractInefficientZeroCheck {

    @Override
    public boolean isTargetMethod(JavaNameOccurrence occ) {
        if (occ.getNameForWhichThisIsAQualifier() != null
                && occ.getNameForWhichThisIsAQualifier().getImage().indexOf("trim") != -1) {
            Node pExpression = occ.getLocation().getParent().getParent();
            if (pExpression.getNumChildren() > 2 && "length".equals(pExpression.getChild(2).getImage())) {
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

        if (node.getNumChildren() > 3) {
            // Check last suffix
            if (!"isEmpty".equals(node.getChild(node.getNumChildren() - 2).getImage())) {
                return data;
            }

            Node prevCall = node.getChild(node.getNumChildren() - 4);
            String target = prevCall.getNumChildren() > 0 ? prevCall.getChild(0).getImage() : prevCall.getImage();
            if (target != null && ("trim".equals(target) || target.endsWith(".trim"))) {
                addViolation(data, node);
            }
        }
        return data;
    }

}

