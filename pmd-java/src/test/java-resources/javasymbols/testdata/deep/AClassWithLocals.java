/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package javasymbols.testdata.deep;

/**
 * @author Clément Fournier
 */
public class AClassWithLocals {

    private static Object foo0 = new Object() {}; // AClassWithLocals$0
    private final Object foo1 = new Object() {
        class Member {} // AClassWithLocals$1$Member
    }; // AClassWithLocals$1

    public AClassWithLocals() {
        class Local { // AClassWithLocals$0Local

        }
        new Object() {}; // // AClassWithLocals$2
    }

    static  {
        class Local { // AClassWithLocals$1Local

        }
        new Object() {}; // // AClassWithLocals$3
    }

    void method() {
        class Local { // AClassWithLocals$2Local

        }
        new Object() {}; // // AClassWithLocals$4
    }
}
