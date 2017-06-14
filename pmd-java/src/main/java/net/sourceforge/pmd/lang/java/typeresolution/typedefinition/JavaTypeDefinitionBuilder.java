package net.sourceforge.pmd.lang.java.typeresolution.typedefinition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class JavaTypeDefinitionBuilder {
    private Class clazz = null;
    private List<JavaTypeDefinition> genericArgs = new ArrayList<>();

    JavaTypeDefinitionBuilder() {}

    public JavaTypeDefinitionBuilder addTypeArg(JavaTypeDefinition arg) {
        genericArgs.add(arg);
        return this;
    }

    public List<JavaTypeDefinition> getTypeArgs() {
        return Collections.unmodifiableList(genericArgs);
    }

    public JavaTypeDefinitionBuilder getTypeArg(int index) {
        genericArgs.get(index);
        return this;
    }

    public JavaTypeDefinitionBuilder setType(Class clazz) {
        this.clazz = clazz;
        return this;
    }

    public JavaTypeDefinition build() {
        return new JavaTypeDefinition(clazz, genericArgs);
    }
}
