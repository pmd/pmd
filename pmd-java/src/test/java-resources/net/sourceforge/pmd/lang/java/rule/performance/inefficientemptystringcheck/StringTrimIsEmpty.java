/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.performance.inefficientemptystringcheck;

public class StringTrimIsEmpty {
    String get() {
        return "foo";
    }

    void bar() {
        if (get().trim().isEmpty()) {
            // violation
        }
        if (this.get().trim().isEmpty()) {
            // violation
        }

        String bar = get();
        if (bar.trim().isEmpty()) {
            // violation
        }
        if (bar.toString().trim().isEmpty()) {
            // violation
        }
    }
}
