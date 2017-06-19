/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.typeresolution.typedefinition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class JavaTypeDefinition implements TypeDefinition {
    private final Class<?> clazz;
    private List<JavaTypeDefinition> genericArgs;
    // contains TypeDefs where only the clazz field is used
    private static Map<Class<?>, JavaTypeDefinition> onlyClassTypeDef = new HashMap<>();

    public Class<?> getType() {
        return clazz;
    }

    public List<JavaTypeDefinition> getGenericArgs() {
        if (genericArgs == null) {
            genericArgs = Collections.unmodifiableList(new ArrayList<JavaTypeDefinition>());
        }

        return genericArgs;
    }

    private JavaTypeDefinition(Class<?> clazz, List<JavaTypeDefinition> genericArgs) {
        this.clazz = clazz;

        if (genericArgs != null) {
            this.genericArgs = Collections.unmodifiableList(genericArgs);
        }
    }

    //           builder part of the class

    public static JavaTypeDefinition build(Class<?> clazz) {
        if (onlyClassTypeDef.containsKey(clazz)) {
            return onlyClassTypeDef.get(clazz);
        }

        JavaTypeDefinition typeDef = new JavaTypeDefinition(clazz, null);

        onlyClassTypeDef.put(clazz, typeDef);

        return typeDef;
    }

    /**
     * @param genericArgs This package private method expects that the genericArgs list has not been leaked,
     *                    meaning the other references have been discarded to ensure immutability.
     */
    /* default */ static JavaTypeDefinition build(Class<?> clazz, List<JavaTypeDefinition> genericArgs) {
        if (genericArgs == null) {
            return build(clazz);
        }
        
        return new JavaTypeDefinition(clazz, genericArgs);
    }

    public static JavaTypeDefinitionBuilder builder(Class<?> clazz) {
        return new JavaTypeDefinitionBuilder().setType(clazz);
    }


    public static JavaTypeDefinitionBuilder builder() {
        return new JavaTypeDefinitionBuilder();
    }
}
