/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf.ast;

import net.sourceforge.pmd.lang.ast.test.BaseParsingHelper;
import net.sourceforge.pmd.lang.vf.VfLanguageModule;

public final class VfParsingHelper extends BaseParsingHelper<VfParsingHelper, ASTCompilationUnit> {

    public static final VfParsingHelper DEFAULT = new VfParsingHelper(Params.getDefaultProcess());

    private VfParsingHelper(Params params) {
        super(VfLanguageModule.NAME, ASTCompilationUnit.class, params);
    }

    @Override
    protected VfParsingHelper clone(Params params) {
        return new VfParsingHelper(params);
    }
}
