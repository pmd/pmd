/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex;

import java.util.logging.Level;
import java.util.logging.Logger;

// TODO(b/243903752)
public final class ApexJorjeLogging {
    // note: that's a static/strong reference in order to avoid that the logger is garbage collected
    // private static final Logger APEX_LOGGER = Logger.getLogger(BaseApexLexer.class.getName());

    private ApexJorjeLogging() {
        // this is a utility class
    }

    public static void disableLogging() {
        // Disable the logging of the ApexLexer, e.g.
        // Jul 16, 2017 8:49:56 PM apex.jorje.parser.impl.BaseApexLexer dedupe
        // INFORMATION: Deduped array ApexLexer.DFA23_transition. Found 7927114 shorts which is 15MB not
        // including array overhead. Removed 7204963 shorts which is 13MB not counting array overhead. Took 18ms.
        // APEX_LOGGER.setLevel(Level.WARNING);
    }
}
