/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.lang.apex.ApexLanguageModule;
import net.sourceforge.pmd.lang.ast.RootNode;
import net.sourceforge.pmd.lang.ast.test.BaseParsingHelper;

public class ApexParsingHelper extends BaseParsingHelper<ApexParsingHelper, RootNode> {

    public static final ApexParsingHelper DEFAULT = new ApexParsingHelper(Params.getDefaultProcess());


    private ApexParsingHelper(Params p) {
        super(ApexLanguageModule.NAME, RootNode.class, p);
    }

    @Override
    protected ApexParsingHelper clone(Params params) {
        return new ApexParsingHelper(params);
    }

}
