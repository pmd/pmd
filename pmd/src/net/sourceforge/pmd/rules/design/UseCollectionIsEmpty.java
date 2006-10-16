/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.rules.design;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.ASTEqualityExpression;
import net.sourceforge.pmd.ast.ASTLiteral;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.ast.SimpleJavaNode;

/**
 * Detect structures like "foo.size() == 0" and suggest replacing them with
 * foo.isEmpty(). Will also find != 0 (replacable with !isEmpty()).
 * 
 * @author Jason Bennett
 */
public class UseCollectionIsEmpty extends AbstractRule {

    public Object visit(ASTEqualityExpression node, Object data) {

        SimpleJavaNode leftSide = (SimpleJavaNode) node.jjtGetChild(0);
        SimpleJavaNode rightSide = (SimpleJavaNode) node.jjtGetChild(1);

        ASTPrimaryPrefix leftPrefix = (ASTPrimaryPrefix) leftSide.getFirstChildOfType(ASTPrimaryPrefix.class);
        ASTPrimarySuffix leftSuffix = (ASTPrimarySuffix) leftSide.getFirstChildOfType(ASTPrimarySuffix.class);
        ASTName leftName = null;

        if (leftPrefix != null) {
            leftName = (ASTName) leftPrefix.getFirstChildOfType(ASTName.class);
        }

        if (leftName == null || leftSuffix == null) {
            return data;
        }

        ASTPrimaryPrefix rightPrefix = (ASTPrimaryPrefix) rightSide.getFirstChildOfType(ASTPrimaryPrefix.class);
        ASTLiteral rightLiteral = null;
        if (rightPrefix != null) {
            rightLiteral = (ASTLiteral) rightPrefix.getFirstChildOfType(ASTLiteral.class);
        }

        if (rightLiteral != null && leftSuffix.isArguments() && leftSuffix.getArgumentCount() == 0
                && leftName.getImage().endsWith(".size") && rightLiteral.hasImageEqualTo("0")) {
            addViolation(data, node);
        }

        return data;
    }
}
