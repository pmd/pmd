/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package javasymbols.testdata.deep;

// SomeClassB is imported
// SomeClassA is imported but shadowed by same package scope
// TestCase1 is imported but shadowed by single type import
// Thread is shadowed by java.lang.Thread and not imported
import javasymbols.testdata.*;
import javasymbols.testdata.TestCase1;


public class TypeImportsOnDemand {
    SomeClassA a;

}
