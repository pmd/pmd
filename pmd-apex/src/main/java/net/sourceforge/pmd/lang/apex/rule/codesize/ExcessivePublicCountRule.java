/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.apex.rule.codesize;

import static apex.jorje.semantic.symbol.type.ModifierTypeInfos.PUBLIC;
import static apex.jorje.semantic.symbol.type.ModifierTypeInfos.STATIC;

import net.sourceforge.pmd.lang.apex.ast.ASTCompilation;
import net.sourceforge.pmd.lang.apex.ast.ASTField;
import net.sourceforge.pmd.lang.apex.ast.ASTMethod;
import net.sourceforge.pmd.lang.apex.ast.AccessNode;
import net.sourceforge.pmd.lang.apex.rule.design.ExcessiveNodeCountRule;
import net.sourceforge.pmd.util.NumericConstants;

/**
 * @author ported to Apex from Java version by aglover
 *         <p/>
 *         Class Name: ExcessivePublicCount
 *         <p/>
 *         Rule attempts to count all public methods and public attributes
 *         defined in a class.
 *         <p/>
 *         If a class has a high number of public operations, it might be wise
 *         to consider whether it would be appropriate to divide it into
 *         subclasses.
 *         <p/>
 *         A large proportion of public members and operations means the class
 *         has high potential to be affected by external classes. Futhermore,
 *         increased effort will be required to thoroughly test the class.
 */
public class ExcessivePublicCountRule extends ExcessiveNodeCountRule {

    public ExcessivePublicCountRule() {
        super(ASTCompilation.class);
        setProperty(MINIMUM_DESCRIPTOR, 45d);
    }

    /**
     * Method counts ONLY public methods.
     */
    public Object visit(ASTMethod node, Object data) {
        return this.getTallyOnAccessType((AccessNode) node.jjtGetParent());
    }

    /**
     * Method counts ONLY public class attributes which are not PUBLIC and
     * static- these usually represent constants....
     */
    public Object visit(ASTField node, Object data) {
        if (node.getNode().getModifierInfo().all(PUBLIC, STATIC)) {
            return NumericConstants.ZERO;
        }
        return this.getTallyOnAccessType((AccessNode) node);
    }

    /**
     * Method counts a node if it is public
     *
     * @param node
     *            The access node.
     * @return Integer 1 if node is public 0 otherwise
     */
    private Integer getTallyOnAccessType(AccessNode node) {
        if (node.isPublic()) {
            return NumericConstants.ONE;
        }
        return NumericConstants.ZERO;
    }
}
