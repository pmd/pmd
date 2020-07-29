/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.ast;

import net.sourceforge.pmd.lang.LanguageVersion;
import net.sourceforge.pmd.lang.ast.CharStream;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument;
import net.sourceforge.pmd.lang.ast.impl.javacc.JjtreeParserAdapter;


public class ModelicaParser extends JjtreeParserAdapter<ASTStoredDefinition> {

    @Override
    protected JavaccTokenDocument newDocument(String fullText) {
        return new ModelicaTokenDocument(fullText);
    }

    @Override
    protected ASTStoredDefinition parseImpl(CharStream cs, String suppressMarker, LanguageVersion languageVersion) throws ParseException {
        return new ModelicaParserImpl(cs).StoredDefinition().setLanguageVersion(languageVersion);
    }

}
