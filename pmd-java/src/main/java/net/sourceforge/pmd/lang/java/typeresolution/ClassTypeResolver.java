/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.typeresolution;

import net.sourceforge.pmd.annotation.InternalApi;
import net.sourceforge.pmd.lang.java.ast.JavaParserVisitorAdapter;
import net.sourceforge.pmd.lang.java.typeresolution.internal.NullableClassLoader;


//
// Helpful reading:
// http://www.janeg.ca/scjp/oper/promotions.html
// http://java.sun.com/docs/books/jls/second_edition/html/conversions.doc.html
//

/**
 * @deprecated Some rules still use this so we keep it around, but it's dysfunctional
 */
@Deprecated
@InternalApi
public class ClassTypeResolver extends JavaParserVisitorAdapter implements NullableClassLoader {

    /**
     * Check whether the supplied class name exists.
     */
    public boolean classNameExists(String fullyQualifiedClassName) {
        return false;
    }

    @Override
    public Class<?> loadClassOrNull(String fullyQualifiedClassName) {
        return null;
    }

}
