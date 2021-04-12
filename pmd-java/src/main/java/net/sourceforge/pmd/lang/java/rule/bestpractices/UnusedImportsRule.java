/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices;

import java.util.HashSet;
import java.util.Set;

import net.sourceforge.pmd.lang.java.ast.internal.ImportWrapper;
import net.sourceforge.pmd.lang.java.rule.codestyle.UnnecessaryImportRule;

@Deprecated
public class UnusedImportsRule extends UnnecessaryImportRule {
    // Note: when removing this from pmd 7, the compiled classes used
    // for tests need to be moved to test/java/.../codestyle/unnecessaryimport

    @Deprecated
    protected Set<ImportWrapper> imports = new HashSet<>();

    @Override
    protected boolean isUnusedImports() {
        return true;
    }
}
