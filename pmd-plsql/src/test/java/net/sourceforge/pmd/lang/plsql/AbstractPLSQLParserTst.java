/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql;

import net.sourceforge.pmd.PmdContextualizedTest;

public abstract class AbstractPLSQLParserTst extends PmdContextualizedTest {

    protected final PlsqlParsingHelper plsql = PlsqlParsingHelper.WITH_PROCESSING.withLanguageRegistry(languageRegistry())
                                                                                 .withResourceContext(getClass());

}
