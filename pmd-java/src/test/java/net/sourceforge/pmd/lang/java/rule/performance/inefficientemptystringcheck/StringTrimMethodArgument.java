/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.performance.inefficientemptystringcheck;

public class StringTrimMethodArgument {
    public String get() {
        return "foo";
    }

    public void bar() {
        String bar = "foo";
        System.out.println(bar.trim().isEmpty()); // violation missing
        System.out.println(bar.trim().length() == 0);
        System.out.println(get().trim().isEmpty());
        System.out.println(get().trim().length() == 0);
        System.out.println(this.get().trim().isEmpty());
        System.out.println(this.get().trim().length() == 0);
    }
}
