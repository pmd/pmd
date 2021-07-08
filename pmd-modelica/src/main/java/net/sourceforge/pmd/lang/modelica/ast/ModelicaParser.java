/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.ast;

import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.impl.javacc.CharStream;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument.TokenDocumentBehavior;
import net.sourceforge.pmd.lang.ast.impl.javacc.JjtreeParserAdapter;


public class ModelicaParser extends JjtreeParserAdapter<ASTStoredDefinition> {

    private static final TokenDocumentBehavior TOKEN_BEHAVIOR = new TokenDocumentBehavior(ModelicaTokenKinds.TOKEN_NAMES);

    @Override
    protected TokenDocumentBehavior tokenBehavior() {
        return TOKEN_BEHAVIOR;
    }

    @Override
    protected ASTStoredDefinition parseImpl(CharStream cs, ParserTask task) throws ParseException {
        return new ModelicaParserImpl(cs).StoredDefinition().makeTaskInfo(task);
    }

}
