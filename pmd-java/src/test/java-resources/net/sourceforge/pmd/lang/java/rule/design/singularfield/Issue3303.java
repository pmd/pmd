/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.design.singularfield;

public class Issue3303 {

    // "private" is a must for reproducing the problem
    private final NoThrowingCloseable first;

    Issue3303(NoThrowingCloseable first) {
        this.first = first;
    }

    public void performClosing() {
        // note: this is uncommented in the test case, it needs
        // java 9
        //        try (first) {
        // this block can be empty or not
        //        }
    }
}
