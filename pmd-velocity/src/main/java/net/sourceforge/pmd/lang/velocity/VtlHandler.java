/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.velocity;

import net.sourceforge.pmd.lang.AbstractPmdLanguageVersionHandler;
import net.sourceforge.pmd.lang.ast.Parser;
import net.sourceforge.pmd.lang.velocity.ast.VtlParser;

public class VtlHandler extends AbstractPmdLanguageVersionHandler {

    @Override
    public Parser getParser() {
        return new VtlParser();
    }

}
