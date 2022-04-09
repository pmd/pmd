/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.ecmascript;

import net.sourceforge.pmd.lang.AbstractPmdLanguageVersionHandler;
import net.sourceforge.pmd.lang.ast.Parser;
import net.sourceforge.pmd.lang.ecmascript.ast.EcmascriptParser;

class EcmascriptHandler extends AbstractPmdLanguageVersionHandler {

    private final int rhinoVersion;

    EcmascriptHandler(int rhinoVersion) {
        this.rhinoVersion = rhinoVersion;
    }

    @Override
    public Parser getParser() {
        return new EcmascriptParser(rhinoVersion);
    }

}
