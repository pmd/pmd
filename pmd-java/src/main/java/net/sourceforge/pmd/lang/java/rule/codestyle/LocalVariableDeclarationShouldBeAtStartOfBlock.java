/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTExplicitConstructorInvocation;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchFallthroughBranch;
import net.sourceforge.pmd.lang.java.ast.ASTSwitchLabel;
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
        // rule does not apply to variables declared and initialized inside for loop initializers
        // it also does not apply to try-with-resources blocks
        if (isInStatementInitializer(declaration)) {
            return data;
        }

        // rule does not apply to variables declared with var keyword
        if (declaration.isTypeInferred()) {
            return data;
        }

        boolean declarationContainsInitialization = containsInitialization(declaration);

        if (!isAtStartOfBlock(declaration) || declarationContainsInitialization) {
            JavaNode child = declaration.getFirstChild();
            ASTVariableId firstFlaggedVarID = null;

            while (child != null) {
                if (child instanceof ASTVariableDeclarator) {
                    ASTVariableDeclarator castedChild = (ASTVariableDeclarator) child;
                    if (!declarationContainsInitialization || castedChild.hasInitializer()) {
                        firstFlaggedVarID = castedChild.getVarId();
                        break;
                    }
                }
                child = child.getNextSibling();
            }

            if (firstFlaggedVarID == null) { // should never be null but just in case
                asCtx(data).addViolation(declaration, "THIS IS NOT SUPPOSED TO HAPPEN");
                return data;
            }
            asCtx(data).addViolation(declaration, firstFlaggedVarID.getName());
        }

        return data;
    }

    private boolean isInStatementInitializer(ASTLocalVariableDeclaration declaration) {
        // this will stop working if a distinct scope can exist inside a new type of statement (not braces or case of switch)
        return !(declaration.getParent() instanceof ASTBlock
                || declaration.getParent() instanceof ASTSwitchFallthroughBranch);
    }

    /*
    Whether any variables in the declaration are initialized
     */
    private boolean containsInitialization(ASTLocalVariableDeclaration declaration) {
        for (JavaNode nthChild: declaration.children()) {
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
            } else if (!(sibling instanceof ASTLocalVariableDeclaration
                    || sibling instanceof ASTSwitchLabel)) {
                return false;
            }

            sibling = sibling.getPreviousSibling();
        }
        return true;
    }
}
