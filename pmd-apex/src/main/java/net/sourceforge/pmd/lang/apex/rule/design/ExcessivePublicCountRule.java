/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.rule.design;

import net.sourceforge.pmd.lang.apex.ast.ASTFieldDeclarationStatements;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.ASTUserClass;
import net.sourceforge.pmd.util.NumericConstants;

/**
 * Rule attempts to count all public methods and public attributes
 * defined in a class.
 *
 * <p>If a class has a high number of public operations, it might be wise
 * to consider whether it would be appropriate to divide it into
 * subclasses.</p>
 *
 * <p>A large proportion of public members and operations means the class
 * has high potential to be affected by external classes. Futhermore,
 * increased effort will be required to thoroughly test the class.</p>
 *
 * @author ported from Java original of aglover
 */
public class ExcessivePublicCountRule extends ExcessiveNodeCountRule {

    public ExcessivePublicCountRule() {
        super(ASTUserClass.class);
        setProperty(MINIMUM_DESCRIPTOR, 20d);
        setProperty(CODECLIMATE_CATEGORIES, "Complexity");
        setProperty(CODECLIMATE_REMEDIATION_MULTIPLIER, 150);
        setProperty(CODECLIMATE_BLOCK_HIGHLIGHTING, false);
    }

    @Override
    public Object visit(ASTMethod node, Object data) {
        if (node.getModifiers().isPublic() && !node.getImage().matches("<clinit>|<init>|clone")) {
            return NumericConstants.ONE;
        }
        return NumericConstants.ZERO;
    }

    @Override
    public Object visit(ASTFieldDeclarationStatements node, Object data) {
        if (node.getModifiers().isPublic() && !node.getModifiers().isStatic()) {
            return NumericConstants.ONE;
        }
        return NumericConstants.ZERO;
    }
}
