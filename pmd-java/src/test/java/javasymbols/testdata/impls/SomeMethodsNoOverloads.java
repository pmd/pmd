/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package javasymbols.testdata.impls;


public class SomeMethodsNoOverloads {

    public final String foo() {
        return "";
    }


    public interface Other {

        default int defaultMethod() {
            return 1;
        }

    }
}
