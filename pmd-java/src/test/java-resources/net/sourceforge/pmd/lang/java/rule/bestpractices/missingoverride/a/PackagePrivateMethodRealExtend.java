/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.bestpractices.missingoverride.a;

public class PackagePrivateMethodRealExtend extends PackagePrivateMethod {

    // package private, does override
    @Override
    void printMessage() {
        System.out.println("Click");
    }
}
