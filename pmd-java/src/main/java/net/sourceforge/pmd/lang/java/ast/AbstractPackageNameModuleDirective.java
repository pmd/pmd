/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

public abstract class AbstractPackageNameModuleDirective extends ASTModuleDirective {

    AbstractPackageNameModuleDirective(int id) {
        super(id);
    }

    public final String getPackageName() {
        return super.getImage();
    }

    @Override
    @Deprecated
    public final String getImage() {
        return null;
    }

    final void setPackageName(String name) {
        super.setImage(name);
    }
}
