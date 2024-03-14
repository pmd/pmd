/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.velocity;

import net.sourceforge.pmd.lang.test.ast.BaseParsingHelper;
import net.sourceforge.pmd.lang.velocity.ast.ASTTemplate;

public final class VtlParsingHelper extends BaseParsingHelper<VtlParsingHelper, ASTTemplate> {

    public static final VtlParsingHelper DEFAULT = new VtlParsingHelper(Params.getDefault());

    private VtlParsingHelper(Params params) {
        super(VtlLanguageModule.getInstance(), ASTTemplate.class, params);
    }

    @Override
    protected VtlParsingHelper clone(Params params) {
        return new VtlParsingHelper(params);
    }
}
