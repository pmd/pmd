/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTFieldDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;

//FUTURE This is not referenced by any RuleSet?
/**
 * @deprecated This is a test rule and should not have been added to the main source. The class will be removed
 *             with PMD 7.
 */
@Deprecated
public class SymbolTableTestRule extends AbstractJavaRule {

    @Override
    public Object visit(ASTFieldDeclaration node, Object data) {
        for (ASTVariableDeclaratorId declaration : node.findDescendantsOfType(ASTVariableDeclaratorId.class)) {
            for (NameOccurrence no : declaration.getUsages()) {
                Node location = no.getLocation();
                System.out.println(declaration.getImage() + " is used here: " + location.getImage());
            }
        }
        return data;
    }
}
