/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package javasymbols.testdata.deep;

/**
 * @author Clément Fournier
 */
public class Another$ClassWith$Dollar {

    public static class AnInner$ClassWithDollar {

        public static class ADeeper$ClassWithDollar {

        }
    }

    // looks like an anonymous class but isn't.
    public static class DollarsAndNumbers$0 {

    }
}
