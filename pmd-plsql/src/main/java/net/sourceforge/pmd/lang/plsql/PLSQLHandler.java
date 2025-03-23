/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.plsql;

import net.sourceforge.pmd.lang.AbstractPmdLanguageVersionHandler;
import net.sourceforge.pmd.lang.ast.Parser;
import net.sourceforge.pmd.lang.plsql.ast.PLSQLParser;

/**
 * Implementation of LanguageVersionHandler for the PLSQL AST. It uses anonymous
 * classes as adapters of the visitors to the VisitorStarter interface.
 *
 * @author sturton - PLDoc - pldoc.sourceforge.net
 */
public class PLSQLHandler extends AbstractPmdLanguageVersionHandler {

    @Override
    public Parser getParser() {
        return new PLSQLParser();
    }

}
