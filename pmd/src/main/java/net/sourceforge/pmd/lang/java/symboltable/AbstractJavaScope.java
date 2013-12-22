/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.java.symboltable;

import java.util.Set;

import net.sourceforge.pmd.lang.symboltable.AbstractScope;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;

/**
 * Provides the basic java scope implementation
 * <p/>
 * @see <a href="http://docs.oracle.com/javase/specs/jls/se7/html/jls-6.html#jls-6.3">JLS 6.3</a>
 */
public abstract class AbstractJavaScope extends AbstractScope {

    @Override
    public void addDeclaration(NameDeclaration declaration) {
        if (declaration instanceof VariableNameDeclaration && getDeclarations().keySet().contains(declaration)) {
            throw new RuntimeException(declaration + " is already in the symbol table");
        }
        super.addDeclaration(declaration);
    }

    @Override
    public boolean contains(NameOccurrence occurrence) {
        return findVariableHere((JavaNameOccurrence)occurrence) != null;
    }

    protected abstract NameDeclaration findVariableHere(JavaNameOccurrence occurrence);

    protected <T> String glomNames(Set<T> s) {
    	StringBuilder result = new StringBuilder();
        for (T t: s) {
            result.append(t.toString());
            result.append(',');
        }
        return result.length() == 0 ? "" : result.toString().substring(0, result.length() - 1);
    }
}
