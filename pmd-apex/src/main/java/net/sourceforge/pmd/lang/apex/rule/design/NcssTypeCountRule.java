/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.design;

import net.sourceforge.pmd.lang.apex.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.lang.apex.ast.ASTUserEnum;
import net.sourceforge.pmd.lang.apex.ast.ASTUserInterface;
import net.sourceforge.pmd.stat.DataPoint;
import net.sourceforge.pmd.util.NumericConstants;

/**
 * Non-commented source statement counter for type declarations.
 *
 * @author ported from Java original of Jason Bennett
 */
public class NcssTypeCountRule extends AbstractNcssCountRule {

    /**
     * Count type declarations. This includes classes as well as enums and
     * annotations.
     */
    public NcssTypeCountRule() {
        super(ASTUserClass.class);
        setProperty(MINIMUM_DESCRIPTOR, 500d);
        setProperty(CODECLIMATE_CATEGORIES, "Complexity");
        setProperty(CODECLIMATE_REMEDIATION_MULTIPLIER, 250);
        setProperty(CODECLIMATE_BLOCK_HIGHLIGHTING, false);
    }

    @Override
    public Object visit(ASTUserClass node, Object data) {

        if (!node.hasDescendantOfAnyType(ASTUserClass.class)) {
            return super.visit(node, data);
        }

        return countNodeChildren(node, data);
    }

    @Override
    public Object visit(ASTUserInterface node, Object data) {

        if (!node.hasDescendantOfAnyType(ASTUserClass.class)) {
            return super.visit(node, data);
        }

        return countNodeChildren(node, data);
    }

    @Override
    public Object visit(ASTUserEnum node, Object data) {
        return countNodeChildren(node, data);
    }

    @Override
    public Object visit(ASTMethod node, Object data) {
        if (!node.getImage().matches("<clinit>|<init>|clone")) {
            return countNodeChildren(node, data);
        }

        return NumericConstants.ZERO;
    }

    @Override
    public Object visit(ASTFieldDeclaration node, Object data) {
        return NumericConstants.ONE;
    }

    @Override
    public Object[] getViolationParameters(DataPoint point) {
        return new String[] { String.valueOf((int) point.getScore()) };
    }
}
