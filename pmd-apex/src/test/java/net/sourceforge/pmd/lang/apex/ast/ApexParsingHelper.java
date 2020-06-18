/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.apex.ApexLanguageModule;
import net.sourceforge.pmd.lang.apex.multifile.ApexMultifileVisitorFacade;
import net.sourceforge.pmd.lang.ast.test.BaseParsingHelper;

public class ApexParsingHelper extends BaseParsingHelper<ApexParsingHelper, ASTApexFile> {

    public static final ApexParsingHelper DEFAULT = new ApexParsingHelper(Params.getDefaultProcess());


    private ApexParsingHelper(Params p) {
        super(ApexLanguageModule.NAME, ASTApexFile.class, p);
    }

    @Override
    protected ApexParsingHelper clone(Params params) {
        return new ApexParsingHelper(params);
    }

    @Override
    protected void postProcessing(LanguageVersionHandler handler, LanguageVersion lversion, ASTApexFile rootNode) {
        super.postProcessing(handler, lversion, rootNode);
        new ApexMultifileVisitorFacade().initializeWith(rootNode);
    }
}
