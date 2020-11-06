/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java;

import net.sourceforge.pmd.lang.ast.test.BaseParsingHelper;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;

public class JavaParsingHelper extends BaseParsingHelper<JavaParsingHelper, ASTCompilationUnit> {

    /** This just runs the parser and no processing stages. */
    public static final JavaParsingHelper JUST_PARSE = new JavaParsingHelper(Params.getDefaultNoProcess());
    /** This runs all processing stages when parsing. */
    public static final JavaParsingHelper WITH_PROCESSING = new JavaParsingHelper(Params.getDefaultProcess());

    private JavaParsingHelper(Params params) {
        super(JavaLanguageModule.NAME, ASTCompilationUnit.class, params);
    }


    @Override
    protected JavaParsingHelper clone(Params params) {
        return new JavaParsingHelper(params);
    }

}
