/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices.missingoverride;

import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTPackageDeclaration;


public class GenericWithOverloadsImpl implements GenericInterfaceWithOverloads<String, Integer> {

    // a bridge method is generated for each of these that implement an interface method


    @Override
    public String visit(ASTCompilationUnit node, String data) {
        return null;
    }


    @Override
    public String visit(ASTPackageDeclaration node, String data) {
        return null;
    }


    @Override
    public String multi(ASTImportDeclaration node, String data, Integer integer) {
        return null;
    }


    // this one is not overriden, no bridge
    public String multi(ASTMethodDeclaration node, String data, Integer integer) {
        return null;
    }


    @Override
    public String multi(ASTPackageDeclaration node, String data, Integer integer) {
        return null;
    }

}
