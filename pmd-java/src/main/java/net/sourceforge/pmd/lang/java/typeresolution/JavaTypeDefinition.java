package net.sourceforge.pmd.lang.java.typeresolution;

import java.util.ArrayList;
import java.util.List;



public class JavaTypeDefinition {
    private Class clazz;
    private List<JavaTypeDefinition> genericArgs = null;

    public JavaTypeDefinition(Class clazz) {
        this.clazz = clazz;
    }

    public Class getType() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public List<JavaTypeDefinition> getGenericArgs() {
        if(genericArgs == null) {
            genericArgs = new ArrayList<>();
        }

        return genericArgs;
    }

    public void setGenericArgs(List<JavaTypeDefinition> genericArgs) {
        this.genericArgs = genericArgs;
    }
}
