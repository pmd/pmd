/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.lang.apex.ApexLanguageModule;
import net.sourceforge.pmd.lang.test.ast.BaseParsingHelper;

public class ApexParsingHelper extends BaseParsingHelper<ApexParsingHelper, ASTApexFile> {

    public static final ApexParsingHelper DEFAULT = new ApexParsingHelper(Params.getDefault());


    private ApexParsingHelper(Params p) {
        super(ApexLanguageModule.getInstance(), ASTApexFile.class, p);
    }

    @Override
    protected ApexParsingHelper clone(Params params) {
        return new ApexParsingHelper(params);
    }

}
