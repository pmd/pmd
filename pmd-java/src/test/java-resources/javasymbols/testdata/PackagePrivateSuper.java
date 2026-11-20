/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package javasymbols.testdata;

/**
 * Has a package-private method, and a subclass in another package:
 * {@link javasymbols.testdata.deep.SubclassOfPackagePrivateSuper}.
 */
public class PackagePrivateSuper {

    void m(long x) { }
}
