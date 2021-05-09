/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

public class ApexParserTestBase {

    protected final ApexParsingHelper apex = ApexParsingHelper.DEFAULT.withResourceContext(getClass());


    protected ASTUserClassOrInterface<?> parse(String code) {
        return apex.parse(code).getMainNode();
    }

    protected ASTUserClassOrInterface<?> parseResource(String code) {
        return apex.parseResource(code).getMainNode();
    }
}
