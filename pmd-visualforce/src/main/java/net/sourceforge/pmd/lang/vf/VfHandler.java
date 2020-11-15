/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf;

import net.sourceforge.pmd.lang.AbstractPmdLanguageVersionHandler;
import net.sourceforge.pmd.lang.Parser;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.vf.ast.VfParser;

public class VfHandler extends AbstractPmdLanguageVersionHandler {

    @Override
    public Parser getParser(ParserOptions parserOptions) {
        return new VfParser();
    }

}
