/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica;

import net.sourceforge.pmd.lang.AbstractPmdLanguageVersionHandler;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.XPathHandler;
import net.sourceforge.pmd.lang.ast.xpath.DefaultASTXPathHandler;
import net.sourceforge.pmd.lang.modelica.ast.ModelicaParser;
import net.sourceforge.pmd.lang.modelica.internal.ModelicaProcessingStage;

public class ModelicaHandler extends AbstractPmdLanguageVersionHandler {

    public ModelicaHandler() {
        super(ModelicaProcessingStage.class);
    }

    @Override
    public XPathHandler getXPathHandler() {
        return new DefaultASTXPathHandler();
    }


    @Override
    public Parser getParser(ParserOptions parserOptions) {
        return new ModelicaParser(parserOptions);
    }

}
