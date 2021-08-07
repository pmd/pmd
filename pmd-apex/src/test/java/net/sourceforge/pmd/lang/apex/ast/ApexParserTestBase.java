/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import apex.jorje.semantic.ast.compilation.Compilation;

public class ApexParserTestBase {

    protected final ApexParsingHelper apex = ApexParsingHelper.DEFAULT.withResourceContext(getClass());


    protected ApexNode<Compilation> parse(String code) {
        return apex.parse(code);
    }

    protected ApexNode<? extends Compilation> parse(String code, String fileName) {
        return apex.parse(code, null, fileName);
    }

    protected ApexNode<Compilation> parseResource(String code) {
        return apex.parseResource(code);
    }
}
