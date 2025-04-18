/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.impl;

import net.sourceforge.pmd.annotation.Experimental;
import net.sourceforge.pmd.lang.AbstractPmdLanguageVersionHandler;
import net.sourceforge.pmd.lang.ast.Parser;

/**
 * Base language version handler for dialect languages that support PMD, i.e. can build an AST
 * and support AST processing stages.
 *
 * @author Juan Martín Sotuyo Dodero
 * @since 7.13.0
 */
@Experimental
public class BasePmdDialectLanguageVersionHandler extends AbstractPmdLanguageVersionHandler {

    @Override
    public final Parser getParser() {
        return null;
    }
}
