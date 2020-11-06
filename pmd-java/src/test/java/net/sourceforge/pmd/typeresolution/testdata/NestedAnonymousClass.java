/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.typeresolution.testdata;

import net.sourceforge.pmd.typeresolution.testdata.dummytypes.Converter;
import net.sourceforge.pmd.typeresolution.testdata.dummytypes.ConverterFactory;

public class NestedAnonymousClass {

    public class Bar {
    }

    public ConverterFactory<String, Bar> factory = new ConverterFactory<String, Bar>() {

        @Override
        public <Z extends Bar> Converter<String, Z> getConverter(Class<Z> targetType) {
            return new Converter<String, Z>() {
                @SuppressWarnings("unchecked")
                @Override
                public Z convert(String source) {
                    return (Z) new Bar();
                }
            };
        }
    };
}
