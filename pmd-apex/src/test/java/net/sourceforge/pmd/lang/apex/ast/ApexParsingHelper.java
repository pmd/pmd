/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.lang.apex.ApexLanguageModule;
import net.sourceforge.pmd.lang.ast.test.BaseParsingHelper;

import com.google.summit.ast.CompilationUnit;

public class ApexParsingHelper extends BaseParsingHelper<ApexParsingHelper, ApexRootNode<CompilationUnit>> {

    public static final ApexParsingHelper DEFAULT = new ApexParsingHelper(Params.getDefaultProcess());


    private ApexParsingHelper(Params p) {
        super(ApexLanguageModule.NAME, (Class<ApexRootNode<CompilationUnit>>) (Class) ApexRootNode.class, p);
    }

    @Override
    protected ApexParsingHelper clone(Params params) {
        return new ApexParsingHelper(params);
    }

}
