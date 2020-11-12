/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.vf;

import net.sourceforge.pmd.lang.ParserOptions;

public class VfParserOptions extends ParserOptions {
    public VfParserOptions() {
        super(new VfLanguageModule());
        definePropertyDescriptor(VfExpressionTypeVisitor.APEX_DIRECTORIES_DESCRIPTOR);
        definePropertyDescriptor(VfExpressionTypeVisitor.OBJECTS_DIRECTORIES_DESCRIPTOR);
        overridePropertiesFromEnv();
    }
}
