/*
 * User: tom
 * Date: Jul 20, 2002
 * Time: 9:28:10 AM
 */
package net.sourceforge.pmd.rules;

import net.sourceforge.pmd.AbstractRule;
import net.sourceforge.pmd.ast.SimpleNode;
import net.sourceforge.pmd.ast.ASTBlock;

public abstract class BracesRule extends AbstractRule {

    protected boolean hasBlockAsFirstChild(SimpleNode node) {
        return (node.jjtGetNumChildren() != 0 && (node.jjtGetChild(0) instanceof ASTBlock));
    }

}
