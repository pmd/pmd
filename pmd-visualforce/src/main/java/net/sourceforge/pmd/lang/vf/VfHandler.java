/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf;

import net.sourceforge.pmd.processor.BatchLanguageProcessor;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.ast.Parser;
import net.sourceforge.pmd.lang.vf.ast.VfParser;

public class VfHandler extends BatchLanguageProcessor<VfLanguageProperties> implements LanguageVersionHandler {

    public VfHandler(VfLanguageProperties bundle) {
        super(bundle);
    }

    @Override
    public Parser getParser() {
        return new VfParser(getProperties());
    }

    @Override
    public LanguageVersionHandler services() {
        return this;
    }
}
