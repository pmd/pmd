/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex.ast;

import com.google.summit.ast.CompilationUnit;

public class ApexParserTestBase {

    protected final ApexParsingHelper apex = ApexParsingHelper.DEFAULT.withResourceContext(getClass());

    protected ApexRootNode<? extends CompilationUnit> parse(String code) {
        return apex.parse(code);
    }

    protected ApexRootNode<? extends CompilationUnit> parse(String code, String fileName) {
        return apex.parse(code, null, fileName);
    }

    protected ApexRootNode<? extends CompilationUnit> parseResource(String code) {
        return apex.parseResource(code);
    }
}
