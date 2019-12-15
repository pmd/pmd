/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.io.Reader;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.ast.AbstractTokenManager;
import net.sourceforge.pmd.lang.ast.JavaCharStream;
import net.sourceforge.pmd.lang.java.ast.internal.LanguageLevelChecker;
import net.sourceforge.pmd.lang.java.qname.JavaOperationQualifiedName;
import net.sourceforge.pmd.lang.java.qname.JavaTypeQualifiedName;
import net.sourceforge.pmd.lang.java.symbols.table.JSymbolTable;
import net.sourceforge.pmd.lang.java.typeresolution.typedefinition.JavaTypeDefinition;
import net.sourceforge.pmd.lang.symboltable.Scope;

/**
 * Acts as a bridge between outer parts of PMD and the restricted access
 * internal API of this package.
 *
 * <p><b>None of this is published API, and compatibility can be broken anytime!</b>
 * Use this only at your own risk.
 *
 * @author Clément Fournier
 * @since 7.0.0
 */
@InternalApi
public final class InternalApiBridge {

    private InternalApiBridge() {

    }


    public static void setSymbolTable(JavaNode node, JSymbolTable table) {
        ((AbstractJavaNode) node).setSymbolTable(table);
    }

    public static void setScope(JavaNode node, Scope scope) {
        ((AbstractJavaNode) node).setScope(scope);
    }

    public static void setComment(JavaNode node, Comment comment) {
        ((AbstractJavaNode) node).comment(comment);
    }

    public static void setQname(ASTAnyTypeDeclaration declaration, JavaTypeQualifiedName qualifiedName) {
        ((AbstractAnyTypeDeclaration) declaration).setQualifiedName(qualifiedName);
    }

    public static void setQname(MethodLikeNode node, JavaOperationQualifiedName qualifiedName) {
        ((AbstractMethodLikeNode) node).setQualifiedName(qualifiedName);
    }

    public static void setTypeDefinition(TypeNode node, JavaTypeDefinition definition) {
        if (node instanceof AbstractJavaTypeNode) {
            ((AbstractJavaTypeNode) node).setTypeDefinition(definition);
        }
    }

    public static ASTCompilationUnit parseInternal(String fileName, Reader source, LanguageLevelChecker<?> checker, ParserOptions options) {
        JavaParser parser = new JavaParser(new JavaCharStream(source));
        String suppressMarker = options.getSuppressMarker();
        if (suppressMarker != null) {
            parser.setSuppressMarker(suppressMarker);
        }
        parser.setJdkVersion(checker.getJdkVersion());
        parser.setPreview(checker.isPreviewEnabled());

        AbstractTokenManager.setFileName(fileName);
        ASTCompilationUnit acu = parser.CompilationUnit();
        acu.setNoPmdComments(parser.getSuppressMap());
        checker.check(acu);
        return acu;
    }

}
