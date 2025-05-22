/*
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package javasymbols.testdata.deep;

/**
 * This class is in sub-package {@code deep}
 * and shouldn't be confused by the class with the same name
 * in parent package.
 *
 * @see <a href="https://github.com/pmd/pmd/issues/913">[java] Incorrect type resolution with classes having the same name #913</a>
 */
public class ClassInDifferentPackage {
}
