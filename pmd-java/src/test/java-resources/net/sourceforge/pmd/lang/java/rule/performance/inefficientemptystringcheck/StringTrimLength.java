/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.performance.inefficientemptystringcheck;

public class StringTrimLength {
    String get() {
        return "foo";
    }

    void bar() {
        if (get().trim().length() == 0) {
            // violation
        }
        if (this.get().trim().length() == 0) {
            // violation
        }

        String bar = get();
        if (bar.trim().length() == 0) {
            // violation
        }
        if (bar.toString().trim().length() == 0) {
            // violation
        }
    }
}
