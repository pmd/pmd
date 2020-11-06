/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.JavaParsingHelper;

/**
 * Base class for tests that usually need processing stages to run when
 * parsing code.
 */
public abstract class BaseParserTest {

    protected final JavaParsingHelper java = JavaParsingHelper.JUST_PARSE.withResourceContext(getClass());
    protected final JavaParsingHelper java5 = java.withDefaultVersion("1.5");
    protected final JavaParsingHelper java8 = java.withDefaultVersion("1.8");
    protected final JavaParsingHelper java9 = java.withDefaultVersion("9");


    protected ASTCompilationUnit parseCode(final String code) {
        return java.parse(code);
    }

    protected <T extends Node> List<T> getNodes(Class<T> target, String code) {
        return JavaParsingHelper.WITH_PROCESSING.getNodes(target, code);
    }
}
