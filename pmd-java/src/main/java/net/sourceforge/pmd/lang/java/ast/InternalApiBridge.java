/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import org.checkerframework.checker.nullness.qual.Nullable;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.java.symbols.JClassSymbol;
import net.sourceforge.pmd.lang.java.symbols.JConstructorSymbol;
import net.sourceforge.pmd.lang.java.symbols.JElementSymbol;
import net.sourceforge.pmd.lang.java.symbols.JMethodSymbol;
import net.sourceforge.pmd.lang.java.symbols.JTypeParameterSymbol;
import net.sourceforge.pmd.lang.java.symbols.JVariableSymbol;
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

    public static <T> void setSymbol(SymbolDeclaratorNode node, JElementSymbol symbol) {
        if (node instanceof ASTMethodDeclaration) {
            ((ASTMethodDeclaration) node).setSymbol((JMethodSymbol) symbol);
        } else if (node instanceof ASTConstructorDeclaration) {
            ((ASTConstructorDeclaration) node).setSymbol((JConstructorSymbol) symbol);
        } else if (node instanceof ASTAnyTypeDeclaration) {
            ((AbstractAnyTypeDeclaration) node).setSymbol((JClassSymbol) symbol);
        } else if (node instanceof ASTVariableDeclaratorId) {
            ((ASTVariableDeclaratorId) node).setSymbol((JVariableSymbol) symbol);
        } else if (node instanceof ASTTypeParameter) {
            ((ASTTypeParameter) node).setSymbol((JTypeParameterSymbol) symbol);
        }
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

    public static void setQname(ASTAnyTypeDeclaration declaration, String binaryName, @Nullable String canon) {
        ((AbstractAnyTypeDeclaration) declaration).setBinaryName(binaryName, canon);
    }

    public static void setTypeDefinition(TypeNode node, JavaTypeDefinition definition) {
        if (node instanceof AbstractJavaTypeNode) {
            ((AbstractJavaTypeNode) node).setTypeDefinition(definition);
        }
    }

}
