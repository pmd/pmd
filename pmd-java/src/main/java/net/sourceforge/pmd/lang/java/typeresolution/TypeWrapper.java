package net.sourceforge.pmd.lang.java.typeresolution;

import java.util.List;

public class TypeWrapper {
    private Class clazz;
    private List<TypeWrapper> genericArgs = null;

    public TypeWrapper(Class clazz) {
        this.clazz = clazz;
    }

    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public List<TypeWrapper> getGenericArgs() {
        return genericArgs;
    }

    public void setGenericArgs(List<TypeWrapper> genericArgs) {
        this.genericArgs = genericArgs;
    }
}
