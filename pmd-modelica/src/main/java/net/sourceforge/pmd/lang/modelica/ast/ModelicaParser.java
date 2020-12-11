/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.ast;

import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.ast.CharStream;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument;
import net.sourceforge.pmd.lang.ast.impl.javacc.JjtreeParserAdapter;


public class ModelicaParser extends JjtreeParserAdapter<ASTStoredDefinition> {

    public ModelicaParser(final ParserOptions parserOptions) {
        super(parserOptions);
    }

    @Override
    protected JavaccTokenDocument newDocument(String fullText) {
        return new ModelicaTokenDocument(fullText);
    }

    @Override
    protected ASTStoredDefinition parseImpl(CharStream cs, ParserOptions options, String fileName) throws ParseException {
        return new ModelicaParserImpl(cs).StoredDefinition();
    }

}
