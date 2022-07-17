/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.rule;


import net.sourceforge.pmd.RuleContext;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.plsql.ast.ASTInput;
import net.sourceforge.pmd.lang.plsql.ast.ASTPackageBody;
import net.sourceforge.pmd.lang.plsql.ast.ASTPackageSpecification;
import net.sourceforge.pmd.lang.plsql.ast.ASTProgramUnit;
import net.sourceforge.pmd.lang.plsql.ast.ASTTriggerUnit;
import net.sourceforge.pmd.lang.plsql.ast.ASTTypeSpecification;
import net.sourceforge.pmd.lang.plsql.ast.ExecutableCode;
import net.sourceforge.pmd.lang.plsql.ast.PLSQLParserVisitor;
import net.sourceforge.pmd.lang.rule.AbstractRule;

public abstract class AbstractPLSQLRule extends AbstractRule implements PLSQLParserVisitor {


    @Override
    public void apply(Node target, RuleContext ctx) {
        target.acceptVisitor(this, ctx);
    }

    /**
     * Gets the Image of the first parent node of type
     * ASTClassOrInterfaceDeclaration or <code>null</code>
     *
     * @param node
     *            the node which will be searched
     */
    protected final String getDeclaringType(Node node) {
        Node c;

        /*
         * Choose the Object Type
         */
        c = node.getFirstParentOfType(ASTPackageSpecification.class);
        if (c != null) {
            return c.getImage();
        }

        c = node.getFirstParentOfType(ASTTypeSpecification.class);
        if (c != null) {
            return c.getImage();
        }

        c = node.getFirstParentOfType(ASTPackageBody.class);
        if (c != null) {
            return c.getImage();
        }

        c = node.getFirstParentOfType(ASTTriggerUnit.class);
        if (c != null) {
            return c.getImage();
        }

        // Finally Schema-level Methods
        c = node.getFirstParentOfType(ASTProgramUnit.class);
        if (c != null) {
            return c.getImage();
        }

        return null;
    }

    public static boolean isQualifiedName(Node node) {
        return node.getImage().indexOf('.') != -1;
    }

    public static boolean importsPackage(ASTInput node, String packageName) {
        return false;
    }

    /*
     * Treat all Executable Code
     */
    public Object visit(ExecutableCode node, Object data) {
        return visitPlsqlNode(node, data);
    }
}
