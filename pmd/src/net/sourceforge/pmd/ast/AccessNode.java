package net.sourceforge.pmd.ast;

public class AccessNode extends SimpleNode implements AccessFlags {
    public AccessNode(int i) {
        super(i);
    }

    public AccessNode(JavaParser parser, int i) {
        super(parser, i);
    }

    protected short accessFlags = 0x0000;

    public void setPublic() {
        accessFlags |= ACC_PUBLIC;
    }

    public boolean isPublic() {
        return (accessFlags & ACC_PUBLIC) > 0;
    }

    public void setPrivate() {
        accessFlags |= ACC_PRIVATE;
    }

    public boolean isPrivate() {
        return (accessFlags & ACC_PRIVATE) > 0;
    }

    public void setProtected() {
        accessFlags |= ACC_PROTECTED;
    }

    public boolean isProtected() {
        return (accessFlags & ACC_PROTECTED) > 0;
    }

    public void setStatic() {
        accessFlags |= ACC_STATIC;
    }

    public boolean isStatic() {
        return (accessFlags & ACC_STATIC) > 0;
    }

    public void setFinal() {
        accessFlags |= ACC_FINAL;
    }

    public boolean isFinal() {
        return (accessFlags & ACC_FINAL) > 0;
    }

    public void setSynchronized() {
        accessFlags |= ACC_SYNCHRONIZED;
    }

    public boolean isSynchronized() {
        return (accessFlags & ACC_SYNCHRONIZED) > 0;
    }

    public void setVolatile() {
        accessFlags |= ACC_VOLATILE;
    }

    public boolean isVolatile() {
        return (accessFlags & ACC_VOLATILE) > 0;
    }

    public void setTransient() {
        accessFlags |= ACC_TRANSIENT;
    }

    public boolean isTransient() {
        return (accessFlags & ACC_TRANSIENT) > 0;
    }

    public void setNative() {
        accessFlags |= ACC_NATIVE;
    }

    public boolean isNative() {
        return (accessFlags & ACC_NATIVE) > 0;
    }


    public void setInterface() {
        accessFlags |= ACC_INTERFACE;
    }

    public boolean isInterface() {
        return (accessFlags & ACC_INTERFACE) > 0;
    }


    public void setAbstract() {
        accessFlags |= ACC_ABSTRACT;
    }

    public boolean isAbstract() {
        return (accessFlags & ACC_ABSTRACT) > 0;
    }


    public void setStrict() {
        accessFlags |= ACC_STRICT;
    }

    public boolean isStrict() {
        return (accessFlags & ACC_STRICT) > 0;
    }


    public void setSuper() {
        accessFlags |= ACC_SUPER;
    }

    public boolean isSuper() {
        return (accessFlags & ACC_SUPER) > 0;
    }

    public boolean isPackagePrivate() {
        return !isPrivate() && !isPublic() && !isProtected();
    }

    public String collectDumpedModifiers(String prefix) {
        String out = toString(prefix) + ":";
        if (isPackagePrivate()) {out += "(package private)";}
        if (isPrivate()) {out += "(private)";}
        if (isPublic()) {out += "(public)";}
        if (isProtected()) {out += "(protected)";}
        if (isAbstract()) {out += "(abstract)";}
        if (isStatic()) {out += "(static)";}
        if (isFinal()) {out += "(final)";}
        if (isSynchronized()) {out += "(synchronized)";}
        if (isNative()) {out += "(native)";}
        if (isStrict()) {out += "(strict)";}
        if (isTransient()) {out += "(transient)";}
        return out;
    }
}
