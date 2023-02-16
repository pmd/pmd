/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.tsql.cpd;

import net.sourceforge.pmd.cpd.AbstractLanguage;

/**
 * @author pguyot@kallisys.net
 */
public class TSqlLanguage extends AbstractLanguage {

    public TSqlLanguage() {
        super("TSql", "tsql", new TSqlTokenizer(), ".sql");
    }
}
