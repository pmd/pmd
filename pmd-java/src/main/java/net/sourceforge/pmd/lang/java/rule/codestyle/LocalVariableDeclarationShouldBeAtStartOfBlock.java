/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import net.sourceforge.pmd.lang.java.ast.ASTForStatement;
import net.sourceforge.pmd.lang.java.ast.ASTForeachStatement;
import net.sourceforge.pmd.lang.java.ast.ASTLocalVariableDeclaration;
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
        // rule does not apply to variables declared and initialised inside for loop initialisers
        if (declaration.getParent() instanceof ASTForStatement || declaration.getParent() instanceof ASTForeachStatement) {
            asCtx(data).addViolation(declaration, "This was a for loop");
            return data;
        }
        return data;
    }
}
