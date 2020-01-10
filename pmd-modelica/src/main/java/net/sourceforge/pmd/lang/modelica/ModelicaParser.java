/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.modelica;

import java.io.Reader;

import net.sourceforge.pmd.lang.AbstractParser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.TokenManager;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.ast.ParseException;
import net.sourceforge.pmd.lang.ast.AbstractTokenManager;
import net.sourceforge.pmd.lang.ast.impl.javacc.CharStreamFactory;


public class ModelicaParser extends AbstractParser {
    public ModelicaParser(final ParserOptions parserOptions) {
        super(parserOptions);
    }

    @Override
    protected TokenManager createTokenManager(Reader source) {
        return new ModelicaTokenManager(source);
    }

    @Override
    public Node parse(String fileName, Reader source) throws ParseException {
        AbstractTokenManager.setFileName(fileName);
        return new net.sourceforge.pmd.lang.modelica.ast.ModelicaParser(CharStreamFactory.simpleCharStream(source)).StoredDefinition();
    }

}
