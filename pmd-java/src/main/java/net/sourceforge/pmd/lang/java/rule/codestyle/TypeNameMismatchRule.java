/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle;

import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTTypeDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;

/**
 * Checks for types whose name does not match the name of the file they are defined in.
 */
public class TypeNameMismatchRule extends AbstractJavaRulechainRule {

    public TypeNameMismatchRule() {
        super(ASTCompilationUnit.class);
    }

    @Override
    public Object visit(final ASTCompilationUnit node, final Object data) {
        String expected = node.getTextDocument().getFileId().getFileName().replaceAll("\\.java$", "");
        node.children(ASTTypeDeclaration.class).forEach(typeDef -> {
            if (!typeDef.getSimpleName().isEmpty() && !typeDef.getSimpleName().equals(expected)) {
                asCtx(data).addViolation(typeDef, typeDef.getSimpleName());
            }
        });
        return null;
    }
}
