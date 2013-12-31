/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.plsql.symboltable;


import net.sourceforge.pmd.lang.plsql.symboltable.ClassNameDeclaration;
import net.sourceforge.pmd.lang.symboltable.AbstractScope;
import net.sourceforge.pmd.lang.symboltable.NameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;


public class SourceFileScope extends AbstractScope {

    private String packageImage;

    public SourceFileScope() {
        this("");
    }

    public SourceFileScope(String image) {
        this.packageImage = image;
    }

    public String getPackageName() {
        return packageImage;
    }

    /**
     * {@inheritDoc}
     * @throws IllegalArgumentException if declaration is not a {@link ClassNameDeclaration}
     */
    @Override
    public void addDeclaration(NameDeclaration declaration) {
        if (!(declaration instanceof ClassNameDeclaration)) {
            throw new IllegalArgumentException("A SourceFileScope can only contain classes.");
        }
        super.addDeclaration(declaration);
    }

    public String toString() {
        return "SourceFileScope: " + getDeclarations().keySet();
    }

    protected NameDeclaration findVariableHere(NameOccurrence occ) {
        ImageFinderFunction finder = new ImageFinderFunction(occ.getImage());
        Applier.apply(finder, getDeclarations().keySet().iterator());
        return finder.getDecl();
    }

}
