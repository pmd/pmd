/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

public abstract class AbstractPackageNameModuleDirective extends ASTModuleDirective {

    private String packageName;

    AbstractPackageNameModuleDirective(int id) {
        super(id);
    }

    public final String getPackageName() {
        return packageName;
    }

    final void setPackageName(String name) {
        packageName = name;
    }
}
