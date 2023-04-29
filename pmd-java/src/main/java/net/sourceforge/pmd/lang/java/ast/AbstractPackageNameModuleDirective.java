/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

abstract class AbstractPackageNameModuleDirective extends ASTModuleDirective {

    AbstractPackageNameModuleDirective(int id) {
        super(id);
    }

    /** Return the package name specified in this directive. */
    public final String getPackageName() {
        return getImageInternal();
    }

    final void setPackageName(String name) {
        super.setImage(name);
    }
}
