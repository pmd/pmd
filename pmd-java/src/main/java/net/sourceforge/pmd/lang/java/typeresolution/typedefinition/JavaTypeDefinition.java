package net.sourceforge.pmd.lang.java.typeresolution.typedefinition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class JavaTypeDefinition implements TypeDefinition {
    private final Class clazz;
    private final List<JavaTypeDefinition> genericArgs;

    public Class getType() {
        return clazz;
    }

    public List<JavaTypeDefinition> getGenericArgs() {
        return genericArgs;
    }

    JavaTypeDefinition(Class clazz, List<JavaTypeDefinition> genericArgs) {
        this.clazz = clazz;

        if (genericArgs == null) {
            genericArgs = new ArrayList<>();
        }

        this.genericArgs = Collections.unmodifiableList(genericArgs);
    }


    //           builder part of the class

    // TODO: possibly add some sort of caching (with like a map or something)
    public static JavaTypeDefinition build(Class clazz) {
        return new JavaTypeDefinition(clazz, null);
    }

    public static JavaTypeDefinitionBuilder builder(Class clazz) {
        return new JavaTypeDefinitionBuilder().setType(clazz);
    }

    public static JavaTypeDefinitionBuilder builder() {
        return new JavaTypeDefinitionBuilder();
    }
}
