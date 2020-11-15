/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica;

import net.sourceforge.pmd.lang.AbstractPmdLanguageVersionHandler;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.modelica.ast.ModelicaParser;
import net.sourceforge.pmd.lang.modelica.internal.ModelicaProcessingStage;

public class ModelicaHandler extends AbstractPmdLanguageVersionHandler {

    public ModelicaHandler() {
        super(ModelicaProcessingStage.class);
    }



    @Override
    public Parser getParser(ParserOptions parserOptions) {
        return new ModelicaParser();
    }

}
