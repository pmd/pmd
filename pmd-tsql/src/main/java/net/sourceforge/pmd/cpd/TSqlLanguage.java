/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.cpd;

/**
 * @author pguyot@kallisys.net
 */
public class TSqlLanguage extends AbstractLanguage {

    public TSqlLanguage() {
        super("TSql", "tsql", new TSqlTokenizer(), ".sql");
    }
}
