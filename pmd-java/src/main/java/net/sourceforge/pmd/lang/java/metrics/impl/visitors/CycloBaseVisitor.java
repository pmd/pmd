/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.metrics.impl.visitors;

import org.apache.commons.lang3.mutable.MutableInt;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.java.ast.ASTCatchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTDoStatement;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTIfStatement;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchLabel;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchStatement;
import net.sourceforge.pmd.lang.java.ast.ASTThrowStatement;
import net.sourceforge.pmd.lang.java.ast.ASTWhileStatement;
import net.sourceforge.pmd.lang.java.ast.JavaParserControllessVisitorAdapter;


/**
 * Visitor calculating cyclo without counting boolean operators.
 *
 * @deprecated Visitor decorators are deprecated because they lead to fragile code.
 *
 * @author Clément Fournier
 * @see net.sourceforge.pmd.lang.java.metrics.impl.CycloMetric
 */
@Deprecated
public class CycloBaseVisitor extends JavaParserControllessVisitorAdapter {

    /** Instance. */
    public static final CycloBaseVisitor INSTANCE = new CycloBaseVisitor();

    @Override
    public Object visit(ASTSwitchStatement node, Object data) {
        int childCount = node.getNumChildren();
        int lastIndex = childCount - 1;

        for (int n = 0; n < lastIndex; n++) {
            Node childNode = node.getChild(n);
            if (childNode instanceof ASTSwitchLabel) {
                // default is not considered a decision (same as "else")
                ASTSwitchLabel sl = (ASTSwitchLabel) childNode;
                if (!sl.isDefault()) {
                    childNode = node.getChild(n + 1);    // check the label is not empty
                    if (childNode instanceof ASTBlockStatement) {
                        ((MutableInt) data).increment();
                    }
                }
            }
        }
        super.visit(node, data);
        return data;
    }


    @Override
    public Object visit(ASTConditionalExpression node, Object data) {
        ((MutableInt) data).increment();
        super.visit(node, data);
        return data;
    }


    @Override
    public Object visit(ASTWhileStatement node, Object data) {
        ((MutableInt) data).increment();
        super.visit(node, data);
        return data;
    }


    @Override
    public Object visit(ASTIfStatement node, Object data) {
        ((MutableInt) data).increment();
        super.visit(node, data);
        return data;
    }


    @Override
    public Object visit(ASTForStatement node, Object data) {
        ((MutableInt) data).increment();
        super.visit(node, data);
        return data;
    }


    @Override
    public Object visit(ASTDoStatement node, Object data) {
        ((MutableInt) data).increment();
        super.visit(node, data);
        return data;
    }


    @Override
    public Object visit(ASTCatchStatement node, Object data) {
        ((MutableInt) data).increment();
        super.visit(node, data);
        return data;
    }


    @Override
    public Object visit(ASTThrowStatement node, Object data) {
        ((MutableInt) data).increment();
        super.visit(node, data);
        return data;
    }

}
