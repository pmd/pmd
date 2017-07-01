/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.typeresolution.testdata.dummytypes;

public interface ConverterFactory<S, T> {
    <U extends T> Converter<S, U> getConverter(Class<U> targetType);
}
