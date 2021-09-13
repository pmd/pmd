/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.kotlin;

import net.sourceforge.pmd.lang.AbstractPmdLanguageVersionHandler;
import net.sourceforge.pmd.lang.ast.Parser;
import net.sourceforge.pmd.lang.kotlin.ast.PmdKotlinParser;


public class KotlinHandler extends AbstractPmdLanguageVersionHandler {

    private final String kotlinRelease;

    public KotlinHandler(String release) {
        kotlinRelease = release;
        // check language version?
    }

    @Override
    public Parser getParser() {
        return new PmdKotlinParser();
    }
}
