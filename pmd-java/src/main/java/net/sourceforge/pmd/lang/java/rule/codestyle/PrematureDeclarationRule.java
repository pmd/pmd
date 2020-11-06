/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import java.util.ArrayList;
import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTBlockStatement;
import net.sourceforge.pmd.lang.java.ast.ASTForInit;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTThrowStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

/**
 * Checks for variables in methods that are defined before they are really
 * needed. A reference is deemed to be premature if it is created ahead of a
 * block of code that doesn't use it that also has the ability to return or
 * throw an exception.
 *
 * @author Brian Remedios
 */
public class PrematureDeclarationRule extends AbstractJavaRule {


    @Override
    public Object visit(ASTLocalVariableDeclaration node, Object data) {

        // is it part of a for-loop declaration?
        if (node.getParent() instanceof ASTForInit) {
            // yes, those don't count
            return super.visit(node, data);
        }

        for (ASTVariableDeclaratorId id : node) {
            for (ASTBlockStatement block : statementsAfter(node)) {
                if (hasReferencesIn(block, id.getVariableName())) {
                    break;
                }

                if (hasExit(block)) {
                    addViolation(data, node);
                    break;
                }
            }
        }


        return super.visit(node, data);
    }


    /**
     * Returns whether the block contains a return call or throws an exception.
     * Exclude blocks that have these things as part of an inner class.
     */
    private boolean hasExit(ASTBlockStatement block) {
        return block.hasDescendantOfAnyType(ASTThrowStatement.class, ASTReturnStatement.class);
    }


    /**
     * Returns whether the variable is mentioned within the statement or not.
     */
    private static boolean hasReferencesIn(ASTBlockStatement block, String varName) {

        // allow for closures on the var
        for (ASTName name : block.findDescendantsOfType(ASTName.class, true)) {
            if (isReference(varName, name.getImage())) {
                return true;
            }
        }
        return false;
    }


    /**
     * Return whether the shortName is part of the compound name by itself or as
     * a method call receiver.
     */
    private static boolean isReference(String shortName, String compoundName) {
        int dotPos = compoundName.indexOf('.');

        return dotPos < 0 ? shortName.equals(compoundName) : shortName.equals(compoundName.substring(0, dotPos));
    }


    /**
     * Returns all the block statements following the given local var declaration.
     */
    private static List<ASTBlockStatement> statementsAfter(ASTLocalVariableDeclaration node) {

        Node blockOrSwitch = node.getParent().getParent();

        int count = blockOrSwitch.getNumChildren();
        int start = node.getParent().getIndexInParent() + 1;

        List<ASTBlockStatement> nextBlocks = new ArrayList<>(count - start);

        for (int i = start; i < count; i++) {
            Node maybeBlock = blockOrSwitch.getChild(i);
            if (maybeBlock instanceof ASTBlockStatement) {
                nextBlocks.add((ASTBlockStatement) maybeBlock);
            }
        }

        return nextBlocks;
    }
}
