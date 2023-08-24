/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vm;

import net.sourceforge.pmd.lang.ast.test.BaseParsingHelper;
import net.sourceforge.pmd.lang.vm.ast.ASTTemplate;

public final class VmParsingHelper extends BaseParsingHelper<VmParsingHelper, ASTTemplate> {

    public static final VmParsingHelper DEFAULT = new VmParsingHelper(Params.getDefault());

    private VmParsingHelper(Params params) {
        super(VmLanguageModule.getInstance(), ASTTemplate.class, params);
    }

    @Override
    protected VmParsingHelper clone(Params params) {
        return new VmParsingHelper(params);
    }
}
