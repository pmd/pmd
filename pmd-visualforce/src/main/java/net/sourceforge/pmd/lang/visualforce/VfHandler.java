/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.visualforce;

import net.sourceforge.pmd.lang.LanguageVersionHandler;
import net.sourceforge.pmd.lang.ast.Parser;
import net.sourceforge.pmd.lang.visualforce.ast.VfParser;

public class VfHandler implements LanguageVersionHandler {

    private final VfLanguageProperties properties;

    public VfHandler(VfLanguageProperties properties) {
        this.properties = properties;
    }

    @Override
    public Parser getParser() {
        return new VfParser(properties);
    }
}
