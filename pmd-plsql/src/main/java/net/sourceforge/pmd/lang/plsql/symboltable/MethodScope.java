/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.plsql.symboltable;

import java.util.List;
import java.util.Map;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.plsql.ast.ASTName;
import net.sourceforge.pmd.lang.plsql.ast.AbstractPLSQLNode;
import net.sourceforge.pmd.lang.symboltable.AbstractScope;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;

public class MethodScope extends AbstractScope {

    private Node node;

    public MethodScope(Node node) {
        this.node = node;
    }

    public MethodScope getEnclosingMethodScope() {
        return this;
    }

    public Map<VariableNameDeclaration, List<NameOccurrence>> getVariableDeclarations() {
        return getDeclarations(VariableNameDeclaration.class);
    }

    @Override
    public NameDeclaration addNameOccurrence(NameOccurrence occ) {
        PLSQLNameOccurrence occurrence = (PLSQLNameOccurrence)occ;
        NameDeclaration decl = findVariableHere(occurrence);
        if (decl != null && !occurrence.isThisOrSuper()) {
            getVariableDeclarations().get(decl).add(occurrence);
            Node n = occurrence.getLocation();
            if (n instanceof ASTName) {
                ((ASTName) n).setNameDeclaration(decl);
            } // TODO what to do with PrimarySuffix case?
        }
        return decl;
    }

    @Override
    public void addDeclaration(NameDeclaration declaration) {
        if (declaration instanceof VariableNameDeclaration && getDeclarations().keySet().contains(declaration)) {
            throw new RuntimeException(declaration + " is already in the symbol table");
        }
        super.addDeclaration(declaration);
    }

    public NameDeclaration findVariableHere(PLSQLNameOccurrence occurrence) {
        if (occurrence.isThisOrSuper() || occurrence.isMethodOrConstructorInvocation()) {
            return null;
        }
        ImageFinderFunction finder = new ImageFinderFunction(occurrence.getImage());
        Applier.apply(finder, getVariableDeclarations().keySet().iterator());
        return finder.getDecl();
    }

    public String getName() {
        return ( (AbstractPLSQLNode) node.jjtGetChild(1) ) .getCanonicalImage();
    }

    public String toString() {
        return "MethodScope:" + getVariableDeclarations().keySet();
    }
}
