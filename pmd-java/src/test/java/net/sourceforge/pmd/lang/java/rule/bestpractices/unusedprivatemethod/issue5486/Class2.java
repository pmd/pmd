/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices.unusedprivatemethod.issue5486;

public class Class2 {
    public Class1 getClass1() {
        return new Class1();
    }
}
