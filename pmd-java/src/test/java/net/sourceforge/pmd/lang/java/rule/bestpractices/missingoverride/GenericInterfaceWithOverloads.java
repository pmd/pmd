/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices.missingoverride;

import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTPackageDeclaration;


public interface GenericInterfaceWithOverloads<T, R extends Number> {

    T visit(ASTCompilationUnit node, T data);


    T visit(ASTPackageDeclaration node, T data);


    T multi(ASTImportDeclaration node, T data, R r);


    T multi(ASTPackageDeclaration node, T data, R r);

}
