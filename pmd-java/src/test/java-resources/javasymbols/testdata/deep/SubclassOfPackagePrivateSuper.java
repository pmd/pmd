/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package javasymbols.testdata.deep;

import javasymbols.testdata.PackagePrivateSuper;

/**
 * Does not inherit {@code PackagePrivateSuper#m(long)}, because it is
 * declared in another package (JLS 8.4.8). Its own subclasses do not
 * inherit it either, even in the package of {@code PackagePrivateSuper}.
 */
public class SubclassOfPackagePrivateSuper extends PackagePrivateSuper { }
