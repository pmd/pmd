/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.typeresolution.typedefinition;

import static net.sourceforge.pmd.lang.java.typeresolution.typedefinition.TypeDefinitionType.LOWER_WILDCARD;

/* default */ class JavaTypeDefinitionLower extends JavaTypeDefinitionUpper {
    private static final JavaTypeDefinition OBJECT_DEFINITION = forClass(Object.class);

    protected JavaTypeDefinitionLower(JavaTypeDefinition... typeList) {
        super(LOWER_WILDCARD, typeList);
    }

    @Override
    protected JavaTypeDefinition firstJavaType() {
        return OBJECT_DEFINITION;
    }
}
