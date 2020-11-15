/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vm;

import net.sourceforge.pmd.lang.AbstractPmdLanguageVersionHandler;
import net.sourceforge.pmd.lang.ParserOptions;
import net.sourceforge.pmd.lang.ast.Parser;
import net.sourceforge.pmd.lang.vm.ast.VmParser;

/**
 * Implementation of LanguageVersionHandler for the VM parser.
 *
 */
public class VmHandler extends AbstractPmdLanguageVersionHandler {


    @Override
    public Parser getParser(final ParserOptions parserOptions) {
        return new VmParser();
    }

}
