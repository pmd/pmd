/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.performance;

import net.sourceforge.pmd.lang.java.ast.ASTEqualityExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.ast.TypeNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.types.TypeTestUtil;

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
public class InefficientEmptyStringCheckRule extends AbstractJavaRule {

    public InefficientEmptyStringCheckRule() {
        addRuleChainVisit(ASTPrimaryExpression.class);
    }

    @Override
    public Object visit(ASTPrimaryExpression node, Object data) {
        if (node.getNumChildren() > 3) {
            // Check last suffix
            String lastSuffix = node.getChild(node.getNumChildren() - 2).getImage();
            if (!("isEmpty".equals(lastSuffix) || "length".equals(lastSuffix) && isComparisonWithZero(node))) {
                return data;
            }

            TypeNode prevCall = (TypeNode) node.getChild(node.getNumChildren() - 4);
            String target = prevCall.getNumChildren() > 0 ? prevCall.getChild(0).getImage() : prevCall.getImage();
            if (target != null && ("trim".equals(target) || target.endsWith(".trim"))
                    && TypeTestUtil.isA(String.class, prevCall)) {
                addViolation(data, node);
            }
        }
        return data;
    }

    private boolean isComparisonWithZero(ASTPrimaryExpression node) {
        if (node.getParent() instanceof ASTEqualityExpression && "==".equals(node.getParent().getImage())) {
            JavaNode other = node.getParent().getChild(1);
            if (node.getIndexInParent() == 1) {
                other = node.getParent().getChild(0);
            }
            if (other instanceof ASTPrimaryExpression && other.getNumChildren() == 1
                    && other.getChild(0) instanceof ASTPrimaryPrefix
                    && other.getChild(0).getNumChildren() == 1
                    && other.getChild(0).getChild(0) instanceof ASTLiteral) {
                ASTLiteral literal = (ASTLiteral) other.getChild(0).getChild(0);
                return literal.isIntLiteral() && "0".equals(literal.getImage());
            }
        }
        return false;
    }
}
