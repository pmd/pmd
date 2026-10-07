/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.rule.codestyle.unnecessarycast;

public class PackagePrivateSuper {

    String packagePrivate() {
        return "pkg";
    }

    public String publicMethod() {
        return "pub";
    }

    String packagePrivateField = "field";
}
