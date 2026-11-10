/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import org.apache.commons.lang3.StringUtils;

import net.sourceforge.pmd.lang.document.FileId;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTPackageDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTTypeDeclaration;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.reporting.RuleContext;

/**
 * Checks for types whose name does not match the name of the file they are defined in.
 *
 * @since 7.28.0
 */
public class TypeNameMismatchRule extends AbstractJavaRulechainRule {

    public TypeNameMismatchRule() {
        super(ASTCompilationUnit.class);
    }

    @Override
    public Object visit(final ASTCompilationUnit node, final Object data) {
        RuleContext ctx = (RuleContext) data;
        FileId fileId = node.getTextDocument().getFileId();
        String expected = fileId.getFileName().replaceAll("\\.java$", "");
        node.children(ASTTypeDeclaration.class).forEach(typeDef -> {
            if (!typeDef.getSimpleName().isEmpty() && !typeDef.getSimpleName().equals(expected)) {
                ctx.addViolation(typeDef, typeDef.getSimpleName());
            }
        });
        ASTPackageDeclaration packageDeclaration = node.children(ASTPackageDeclaration.class).first();
        if (packageDeclaration != null) {
            String absolutePath = fileId.getAbsolutePath();
            String parentFolder = absolutePath.substring(0, StringUtils.lastIndexOfAny(absolutePath, "/", "\\"));
            if (!parentFolder.replaceAll("[/\\\\]", ".").endsWith(packageDeclaration.getName())) {
                ctx.addViolationWithMessage(packageDeclaration,
                        "File path does not match package " + packageDeclaration.getName());
            }
        }
        return null;
    }
}
