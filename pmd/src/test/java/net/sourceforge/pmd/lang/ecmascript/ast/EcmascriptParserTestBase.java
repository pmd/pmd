/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */
package net.sourceforge.pmd.lang.ecmascript.ast;

import java.io.Reader;
import java.io.StringReader;

import net.sourceforge.pmd.lang.ecmascript.EcmascriptParserOptions;

import org.mozilla.javascript.ast.AstRoot;

public abstract class EcmascriptParserTestBase {
    public EcmascriptNode<AstRoot> parse(String code) {
        EcmascriptParser parser = new EcmascriptParser(new EcmascriptParserOptions());
        Reader sourceCode = new StringReader(code);
        return parser.parse(sourceCode);
    }
}
