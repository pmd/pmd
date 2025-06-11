/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package javasymbols.testdata;

/**
 * This class in this package shouldn't be confused by the class
 * with the same name in sub-package {@code deep}.
 *
 * @see <a href="https://github.com/pmd/pmd/issues/913">[java] Incorrect type resolution with classes having the same name #913</a>
 */
public class ClassInDifferentPackage {}
