/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symboltable;

import java.util.Set;

import net.sourceforge.pmd.lang.symboltable.AbstractScope;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;

/**
 * Provides the basic java scope implementation.
 *
 * @see <a href="http://docs.oracle.com/javase/specs/jls/se7/html/jls-6.html#jls-6.3">JLS 6.3</a>
 */
public abstract class AbstractJavaScope extends AbstractScope {

    @Override
    public void addDeclaration(NameDeclaration declaration) {
        checkForDuplicatedNameDeclaration(declaration);
        super.addDeclaration(declaration);
    }

    protected void checkForDuplicatedNameDeclaration(NameDeclaration declaration) {
        // Don't throw an exception, type tests patterns are tricky to implement
        // and given it's a preview feature, and the sym table will be replaced
        // for 7.0, it's not useful to support them.

        // if (declaration instanceof VariableNameDeclaration && getDeclarations().keySet().contains(declaration)) {
        //     throw new RuntimeException(declaration + " is already in the symbol table");
        // }
    }

    @Override
    public boolean contains(NameOccurrence occurrence) {
        return !findVariableHere((JavaNameOccurrence) occurrence).isEmpty();
    }

    protected abstract Set<NameDeclaration> findVariableHere(JavaNameOccurrence occurrence);

    protected <T> String glomNames(Set<T> s) {
        StringBuilder result = new StringBuilder();
        for (T t : s) {
            result.append(t.toString());
            result.append(',');
        }
        return result.length() == 0 ? "" : result.toString().substring(0, result.length() - 1);
    }
}
