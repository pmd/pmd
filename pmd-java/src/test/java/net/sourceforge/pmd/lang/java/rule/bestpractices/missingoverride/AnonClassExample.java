/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices.missingoverride;

public class AnonClassExample {
    static {
        new Thread(new Runnable() {
            // missing
            public void run() {

            }
        }).start();
    }
}

