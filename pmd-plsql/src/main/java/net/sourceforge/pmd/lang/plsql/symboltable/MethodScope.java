/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql.symboltable;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.plsql.ast.ASTName;
import net.sourceforge.pmd.lang.plsql.ast.AbstractPLSQLNode;
import net.sourceforge.pmd.lang.symboltable.AbstractScope;
import net.sourceforge.pmd.lang.symboltable.Applier;
import net.sourceforge.pmd.lang.symboltable.ImageFinderFunction;
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
    public Set<NameDeclaration> addNameOccurrence(NameOccurrence occ) {
        PLSQLNameOccurrence occurrence = (PLSQLNameOccurrence) occ;
        Set<NameDeclaration> declarations = findVariableHere(occurrence);
        if (!declarations.isEmpty() && !occurrence.isThisOrSuper()) {
            for (NameDeclaration decl : declarations) {
                getVariableDeclarations().get(decl).add(occurrence);
                Node n = occurrence.getLocation();
                if (n instanceof ASTName) {
                    ((ASTName) n).setNameDeclaration(decl);
                } // TODO what to do with PrimarySuffix case?
            }
        }
        return declarations;
    }

    @Override
    public void addDeclaration(NameDeclaration declaration) {
        if (declaration instanceof VariableNameDeclaration && getDeclarations().keySet().contains(declaration)) {
            throw new RuntimeException(declaration + " is already in the symbol table");
        }
        super.addDeclaration(declaration);
    }

    public Set<NameDeclaration> findVariableHere(PLSQLNameOccurrence occurrence) {
        Set<NameDeclaration> result = new HashSet<>();
        if (occurrence.isThisOrSuper() || occurrence.isMethodOrConstructorInvocation()) {
            return result;
        }
        ImageFinderFunction finder = new ImageFinderFunction(occurrence.getImage());
        Applier.apply(finder, getVariableDeclarations().keySet().iterator());
        if (finder.getDecl() != null) {
            result.add(finder.getDecl());
        }
        return result;
    }

    public String getName() {
        return ((AbstractPLSQLNode) node.getChild(1)).getCanonicalImage();
    }

    @Override
    public String toString() {
        return "MethodScope:" + getVariableDeclarations().keySet();
    }
}
