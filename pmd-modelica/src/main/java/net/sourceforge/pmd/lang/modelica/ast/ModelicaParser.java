/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica.ast;

import net.sourceforge.pmd.benchmark.TimeTracker;
import net.sourceforge.pmd.lang.ast.CharStream;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.impl.javacc.JavaccTokenDocument;
import net.sourceforge.pmd.lang.ast.impl.javacc.JjtreeParserAdapter;
import net.sourceforge.pmd.lang.document.TextDocument;
import net.sourceforge.pmd.lang.modelica.resolver.ModelicaSymbolFacade;


public class ModelicaParser extends JjtreeParserAdapter<ASTStoredDefinition> {

    @Override
    protected JavaccTokenDocument newDocumentImpl(TextDocument textDocument) {
        return new ModelicaTokenDocument(textDocument);
    }

    @Override
    protected ASTStoredDefinition parseImpl(CharStream cs, ParserTask task) throws ParseException {
        ASTStoredDefinition root = new ModelicaParserImpl(cs).StoredDefinition().makeTaskInfo(task);
        TimeTracker.bench("Modelica symbols", () -> ModelicaSymbolFacade.process(root));
        return root;
    }

}
