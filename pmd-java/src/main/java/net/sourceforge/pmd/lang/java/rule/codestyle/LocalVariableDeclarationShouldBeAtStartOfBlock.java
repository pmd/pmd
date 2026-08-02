/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTExplicitConstructorInvocation;
import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTForeachStatement;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;
import net.sourceforge.pmd.lang.java.ast.ASTVariableId;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.properties.PropertyDescriptor;
import net.sourceforge.pmd.properties.PropertyFactory;

public class LocalVariableDeclarationShouldBeAtStartOfBlock extends AbstractJavaRulechainRule {

    private static final PropertyDescriptor<Boolean> REQUIRE_BEFORE_THIS_SUPER =
            PropertyFactory.booleanProperty("requireBeforeThisSuper")
                    .desc("Require that variable declaration comes before super(...) and this(...) calls. Must be set to false in Java24 and below.")
                    .defaultValue(true)
                    .build();

    public LocalVariableDeclarationShouldBeAtStartOfBlock() {
        super(ASTLocalVariableDeclaration.class);
        definePropertyDescriptor(REQUIRE_BEFORE_THIS_SUPER);
    }

    @Override
    public Object visit(ASTLocalVariableDeclaration declaration, Object data) {
        JavaNode parent = declaration.getParent(); // parent cannot be null here

        // rule does not apply to variables declared and initialized inside for loop initializers
        if (parent.getParent() instanceof ASTForStatement || parent.getParent() instanceof ASTForeachStatement) {
            return data;
        }

        // rule does not apply to variables declared with var keyword
        if (declaration.isTypeInferred()) {
            return data;
        }

        if (parent instanceof ASTBlock) {

            if (!isAtStartOfBlock(declaration)) {
                ASTVariableId firstVarID = declaration.getVarIds().first();
                if (firstVarID == null) { // should never be null but just in case
                    return data;
                }
                asCtx(data).addViolation(declaration, firstVarID.getName());
            }
        }

        return data;
    }

    private boolean isAtStartOfBlock(ASTLocalVariableDeclaration declaration) {
        JavaNode sibling = declaration;
        while (sibling != null) {

            if (sibling instanceof ASTLocalVariableDeclaration) {
                ASTLocalVariableDeclaration siblingDecl = (ASTLocalVariableDeclaration) sibling;
                // declaration cannot not have a declarator so there should be no risk of NPE
                ASTVariableDeclarator declarator = (ASTVariableDeclarator) siblingDecl.getLastChild();
                // if declarator includes more than just variable name (also includes expression)
                if (declarator.getNumChildren() != 1) {
                    return false;
                }
            } else if (sibling instanceof ASTExplicitConstructorInvocation) {
                // super or this
                if (getProperty(REQUIRE_BEFORE_THIS_SUPER)) {
                    return false;
                }
            } else {
                return false;
            }

            sibling = sibling.getPreviousSibling();
        }
        return true;
    }
}
