/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.performance.stringtostring;

import java.util.Locale;

public class User {

    public String getName() {
        return "username";
    }

    private String convert(String s) {
        return s.toLowerCase(Locale.ROOT);
    }
}
