/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.performance;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAllocationExpression;
import net.sourceforge.pmd.lang.java.ast.ASTArrayDimsAndInits;
import net.sourceforge.pmd.lang.java.ast.ASTBooleanLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTLiteral;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryExpression;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRule;
import net.sourceforge.pmd.lang.java.typeresolution.TypeHelper;

/**
 * Avoid instantiating Boolean objects; you can reference Boolean.TRUE,
 * Boolean.FALSE, or call Boolean.valueOf() instead.
 *
 * <pre>
 *  public class Foo {
 *       Boolean bar = new Boolean("true");    // just do a Boolean
 *       bar = Boolean.TRUE;                   //ok
 *       Boolean buz = Boolean.valueOf(false); // just do a Boolean buz = Boolean.FALSE;
 *  }
 * </pre>
 */
public class BooleanInstantiationRule extends AbstractJavaRule {

    /*
     * see bug 1744065 : If somebody create it owns Boolean, the rule should not
     * be triggered Therefore, we use this boolean to flag if the source code
     * contains such an import
     *
     */
    private boolean customBoolean;

    @Override
    public Object visit(ASTCompilationUnit decl, Object data) {
        // customBoolean needs to be reset for each new file
        customBoolean = false;

        return super.visit(decl, data);
    }

    @Override
    public Object visit(ASTImportDeclaration decl, Object data) {
        // If the import actually import a Boolean class that overrides
        // java.lang.Boolean
        if (decl.getImportedName().endsWith("Boolean") && !decl.getImportedName().equals("java.lang")) {
            customBoolean = true;
        }
        return super.visit(decl, data);
    }

    @Override
    public Object visit(ASTAllocationExpression node, Object data) {

        if (!customBoolean) {
            if (node.hasDescendantOfType(ASTArrayDimsAndInits.class)) {
                return super.visit(node, data);
            }

            Node n1 = node.getFirstChildOfType(ASTClassOrInterfaceType.class);
            if (TypeHelper.isA((ASTClassOrInterfaceType) n1, Boolean.class)) {
                super.addViolation(data, node);
                return data;
            }
        }
        return super.visit(node, data);
    }

    @Override
    public Object visit(ASTPrimaryPrefix node, Object data) {

        if (!customBoolean) {
            if (node.getNumChildren() == 0 || !(node.getChild(0) instanceof ASTName)) {
                return super.visit(node, data);
            }

            if ("Boolean.valueOf".equals(((ASTName) node.getChild(0)).getImage())
                    || "java.lang.Boolean.valueOf".equals(((ASTName) node.getChild(0)).getImage())) {
                ASTPrimaryExpression parent = (ASTPrimaryExpression) node.getParent();
                ASTPrimarySuffix suffix = parent.getFirstDescendantOfType(ASTPrimarySuffix.class);
                if (suffix == null) {
                    return super.visit(node, data);
                }
                ASTPrimaryPrefix prefix = suffix.getFirstDescendantOfType(ASTPrimaryPrefix.class);
                if (prefix == null) {
                    return super.visit(node, data);
                }

                if (prefix.hasDescendantOfType(ASTBooleanLiteral.class)) {
                    super.addViolation(data, node);
                    return data;
                }
                ASTLiteral literal = prefix.getFirstDescendantOfType(ASTLiteral.class);
                if (literal != null
                        && ("\"true\"".equals(literal.getImage()) || "\"false\"".equals(literal.getImage()))) {
                    super.addViolation(data, node);
                    return data;
                }
            }
        }
        return super.visit(node, data);
    }
}
