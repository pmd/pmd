/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import net.sourceforge.pmd.lang.apex.ApexLanguageModule;
import net.sourceforge.pmd.lang.ast.test.BaseParsingHelper;

import com.google.summit.ast.declaration.TypeDeclaration;

public class ApexParsingHelper extends BaseParsingHelper<ApexParsingHelper, ApexRootNode<TypeDeclaration>> {

    public static final ApexParsingHelper DEFAULT = new ApexParsingHelper(Params.getDefaultProcess());


    private ApexParsingHelper(Params p) {
        super(ApexLanguageModule.NAME, (Class<ApexRootNode<TypeDeclaration>>) (Class) ApexRootNode.class, p);
    }

    @Override
    protected ApexParsingHelper clone(Params params) {
        return new ApexParsingHelper(params);
    }

}
