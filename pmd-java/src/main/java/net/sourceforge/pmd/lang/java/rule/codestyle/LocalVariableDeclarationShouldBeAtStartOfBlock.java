/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

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

        if (!isAtStartOfBlock(declaration) || containsInitialization(declaration)) {
            ASTVariableId firstVarID = declaration.getVarIds().first();
            if (firstVarID == null) { // should never be null but just in case
                return data;
            }
            asCtx(data).addViolation(declaration, firstVarID.getName());
        }

        return data;
    }

    /*
    Whether any variables in the declaration are initialized
     */
    private boolean containsInitialization(ASTLocalVariableDeclaration declaration) {
        for (int childNum = 0; childNum < declaration.getNumChildren(); childNum++) {
            JavaNode nthChild = declaration.getChild(childNum);

            if (nthChild instanceof ASTVariableDeclarator) {
                ASTVariableDeclarator declarator = (ASTVariableDeclarator) nthChild;

                if (declarator.getInitializer() != null) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isAtStartOfBlock(ASTLocalVariableDeclaration declaration) {

        JavaNode sibling = declaration.getPreviousSibling();
        while (sibling != null) {

            if (sibling instanceof ASTExplicitConstructorInvocation) {
                // super or this
                if (getProperty(REQUIRE_BEFORE_THIS_SUPER)) {
                    return false;
                }
            } else if (!(sibling instanceof ASTLocalVariableDeclaration)) {
                return false;
            }

            sibling = sibling.getPreviousSibling();
        }
        return true;
    }
}
