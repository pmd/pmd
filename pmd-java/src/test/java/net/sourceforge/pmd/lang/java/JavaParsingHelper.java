/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java;

import java.util.ArrayList;
import java.util.List;

import org.checkerframework.checker.nullness.qual.NonNull;

import net.sourceforge.pmd.lang.ast.Node;
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


    @NonNull
    @Override
    protected JavaParsingHelper clone(Params params) {
        return new JavaParsingHelper(params);
    }

    public static <T> List<T> convertList(List<Node> nodes, Class<T> target) {
        List<T> converted = new ArrayList<>();
        for (Node n : nodes) {
            converted.add(target.cast(n));
        }
        return converted;
    }
}
