/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.apex;

import net.sourceforge.pmd.internal.Slf4jSimpleConfiguration;

import apex.jorje.parser.impl.BaseApexLexer;

public final class ApexJorjeLogging {

    private ApexJorjeLogging() {
        // this is a utility class
    }

    public static void disableLogging() {
        // Disable the logging of the ApexLexer, e.g.
        // Jul 16, 2017 8:49:56 PM apex.jorje.parser.impl.BaseApexLexer dedupe
        // INFORMATION: Deduped array ApexLexer.DFA23_transition. Found 7927114 shorts which is 15MB not
        // including array overhead. Removed 7204963 shorts which is 13MB not counting array overhead. Took 18ms.
        Slf4jSimpleConfiguration.installJulBridge();
        Slf4jSimpleConfiguration.disableLogging(BaseApexLexer.class);
    }
}
