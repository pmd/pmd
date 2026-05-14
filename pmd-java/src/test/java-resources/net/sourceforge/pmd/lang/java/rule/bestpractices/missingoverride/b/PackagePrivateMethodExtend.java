/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices.missingoverride.b;

import net.sourceforge.pmd.lang.java.rule.bestpractices.missingoverride.a.PackagePrivateMethod;

public class PackagePrivateMethodExtend extends PackagePrivateMethod {
    // does not override
    void printMessage() {
        System.out.println("Hack");
    }
}
