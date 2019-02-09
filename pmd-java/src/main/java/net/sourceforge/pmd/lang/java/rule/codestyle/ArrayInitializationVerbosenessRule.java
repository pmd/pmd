/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import net.sourceforge.pmd.lang.java.ast.ASTAllocationExpression;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

public class ArrayInitializationVerbosenessRule extends AbstractJavaRule {

    @Override
    public Object visit(ASTLocalVariableDeclaration node, Object data) {
        boolean isArrayPlaceholder = true;

        if (isArrayPlaceholder && node.getImage() != null) {
            Pattern verbosePattern = Pattern.compile("(= ?new).+\\{");
            Matcher matcher = verbosePattern.matcher(node.getImage());

            while (matcher.find()) {
                addViolation(data, node);
            }
        }
        return super.visit(node, data);
    }
}
