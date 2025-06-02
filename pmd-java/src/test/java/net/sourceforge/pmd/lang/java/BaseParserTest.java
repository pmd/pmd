/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java;

import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;

/**
 * Base class for tests that usually need processing stages to run when
 * parsing code.
 */
public abstract class BaseParserTest {

    protected final JavaParsingHelper java = JavaParsingHelper.DEFAULT.withResourceContext(getClass());
    protected final JavaParsingHelper java5 = java.withDefaultVersion("1.5");
    protected final JavaParsingHelper java8 = java.withDefaultVersion("1.8");
    protected final JavaParsingHelper java9 = java.withDefaultVersion("9");


    protected ASTCompilationUnit parseCode(final String code) {
        return java.parse(code);
    }

    /**
     * Parse and return an expression. Some variables are predeclared.
     */
    protected ASTExpression parseExpr(String expr) {
        ASTCompilationUnit ast = java.parse("class Foo {{ "
                                                + "String s1,s2,s3; "
                                                + "int i,j,k; "
                                                + "Object o = (" + expr + "); }}");
        return ast.descendants(ASTExpression.class).crossFindBoundaries().firstOrThrow();
    }
}
