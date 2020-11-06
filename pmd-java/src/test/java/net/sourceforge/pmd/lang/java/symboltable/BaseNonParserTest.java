/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.symboltable;

import java.util.List;

import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.JavaParsingHelper;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;

/**
 * Base class for tests that usually need processing stages to run when
 * parsing code.
 */
public abstract class BaseNonParserTest {

    protected final JavaParsingHelper java = JavaParsingHelper.WITH_PROCESSING.withResourceContext(getClass());
    protected final JavaParsingHelper java5 = java.withDefaultVersion("1.5");


    protected ASTCompilationUnit parseCode(final String code) {
        return java.parse(code);
    }

    protected <T extends Node> List<T> getOrderedNodes(Class<T> target, String code) {
        return JavaParsingHelper.WITH_PROCESSING.getNodes(target, code);
    }
}
