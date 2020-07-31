/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.types.testdata;

public class BoolLogic {

    static False FALSE() { // SUPPRESS CHECKSTYLE NOW
        return new False() { };
    }

    static True TRUE() { // SUPPRESS CHECKSTYLE NOW
        return new True() { };
    }

    static False and(False a, False b) {
        return a;
    }

    static False and(True a, False b) {
        return b;
    }

    static False and(False a, True b) {
        return a;
    }

    static True and(True a, True b) {
        return a;
    }

    static False or(False a, False b) {
        return a;
    }

    static True or(True a, False b) {
        return a;
    }

    static True or(False a, True b) {
        return b;
    }

    static True or(True a, True b) {
        return a;
    }

    interface Bool { }

    interface False extends Bool { }

    interface True extends Bool { }

    private BoolLogic() {
    }
}
