/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript.internal;

import org.mozilla.javascript.Context;

import net.sourceforge.pmd.lang.BatchLanguageProcessor;
import net.sourceforge.pmd.lang.LanguagePropertyBundle;
import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.ast.Parser;
import net.sourceforge.pmd.lang.ecmascript.EcmascriptLanguageModule;
import net.sourceforge.pmd.lang.ecmascript.ast.EcmascriptParser;

public class EcmascriptProcessor extends BatchLanguageProcessor<LanguagePropertyBundle>
    implements LanguageVersionHandler {

    public EcmascriptProcessor(LanguagePropertyBundle properties) {
        super(EcmascriptLanguageModule.getInstance(), properties);
    }

    public int getRhinoVersion() {
        return Context.VERSION_ES6;
    }

    @Override
    public  LanguagePropertyBundle getProperties() {
        return super.getProperties();
    }

    @Override
    public Parser getParser() {
        return new EcmascriptParser(this);
    }

}
