/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAnyTypeDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTConditionalExpression;
import net.sourceforge.pmd.lang.java.ast.ASTEqualityExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTNullLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.ASTReturnStatement;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;

/**
 * Utility methods for the package
 *
 * Created on Jan 17, 2005
 *
 * @author mgriffa
 */
public abstract class AbstractSunSecureRule extends AbstractJavaRule {

    /**
     * Tells if the type declaration has a field with varName.
     *
     * @param varName
     *            the name of the field to search
     * @param typeDeclaration
     *            the type declaration
     * @return <code>true</code> if there is a field in the type declaration
     *         named varName, <code>false</code> in other case
     */
    protected final boolean isField(String varName, ASTAnyTypeDeclaration typeDeclaration) {
        final List<ASTFieldDeclaration> fds = typeDeclaration.findDescendantsOfType(ASTFieldDeclaration.class);
        if (fds != null) {
            for (ASTFieldDeclaration fd : fds) {
                final ASTVariableDeclaratorId vid = fd.getFirstDescendantOfType(ASTVariableDeclaratorId.class);
                if (vid != null && vid.hasImageEqualTo(varName)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Gets the name of the variable returned. Some examples: <br>
     * for this.foo returns foo <br>
     * for foo returns foo <br>
     * for foo.bar returns foo.bar
     *
     * @param ret
     *            a return statement to evaluate
     * @return the name of the variable associated or <code>null</code> if it
     *         cannot be detected
     */
    protected final String getReturnedVariableName(ASTReturnStatement ret) {
        if (hasTernaryCondition(ret) && hasTernaryNullCheck(ret)) {
            return ret.getFirstDescendantOfType(ASTConditionalExpression.class).getChild(0)
                    .getFirstDescendantOfType(ASTName.class).getImage();
        }

        final ASTName n = ret.getFirstDescendantOfType(ASTName.class);
        if (n != null) {
            return n.getImage();
        }
        final ASTPrimarySuffix ps = ret.getFirstDescendantOfType(ASTPrimarySuffix.class);
        if (ps != null) {
            return ps.getImage();
        }
        return null;
    }

    private boolean hasTernaryNullCheck(ASTReturnStatement ret) {
        ASTConditionalExpression condition = ret.getFirstDescendantOfType(ASTConditionalExpression.class);
        return condition.getChild(0) instanceof ASTEqualityExpression
                && condition.getChild(0).hasImageEqualTo("==")
                && condition.getChild(0).getChild(0).hasDescendantOfType(ASTName.class)
                && condition.getChild(0).getChild(1).hasDescendantOfType(ASTNullLiteral.class);
    }

    private boolean hasTernaryCondition(ASTReturnStatement ret) {
        return null != ret.getFirstDescendantOfType(ASTConditionalExpression.class);
    }

    /**
     * TODO modify usages to use symbol table Tells if the variable name is a
     * local variable declared in the method.
     *
     * @param vn
     *            the variable name
     * @param node
     *            the ASTMethodDeclaration where the local variable name will be
     *            searched
     * @return <code>true</code> if the method declaration contains any local
     *         variable named vn and <code>false</code> in other case
     */
    protected boolean isLocalVariable(String vn, Node node) {
        final List<ASTLocalVariableDeclaration> lvars = node.findDescendantsOfType(ASTLocalVariableDeclaration.class);
        if (lvars != null) {
            for (ASTLocalVariableDeclaration lvd : lvars) {
                final ASTVariableDeclaratorId vid = lvd.getFirstDescendantOfType(ASTVariableDeclaratorId.class);
                if (vid != null && vid.hasImageEqualTo(vn)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Gets the image of the ASTName node found by
     * {@link Node#getFirstDescendantOfType(Class)} if it is the greatgrandchild
     * of the given node.
     *
     * E.g.
     *
     * <pre>
     * n = Expression || StatementExpression
     *       PrimaryExpression
     *         PrimaryPrefix
     *           Name
     * </pre>
     *
     * @param n
     *            the node to search
     * @return the image of the first ASTName or <code>null</code>
     */
    protected String getFirstNameImage(Node n) {
        ASTName name = n.getFirstDescendantOfType(ASTName.class);
        if (name != null && name.getNthParent(3) == n) {
            return name.getImage();
        }
        return null;
    }

}
