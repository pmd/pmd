package net.sourceforge.pmd.ast;

public class AccessNode extends SimpleNode implements AccessFlags {
    public AccessNode(int i) {
        super(i);
    }

    public AccessNode(JavaParser parser, int i) {
        super(parser, i);
    }

    protected short accessFlags = 0x0000;

    public void setPublic(boolean value) {
        if (value) {
            accessFlags |= ACC_PUBLIC;
        } else {
            accessFlags &= ~ACC_PUBLIC;
        }
    }

    public boolean isPublic() {
        return (accessFlags & ACC_PUBLIC) > 0;
    }

    public void setPrivate(boolean value) {
        if (value) {
            accessFlags |= ACC_PRIVATE;
        } else {
            accessFlags &= ~ACC_PRIVATE;
        }
    }

    public boolean isPrivate() {
        return (accessFlags & ACC_PRIVATE) > 0;
    }

    public void setProtected(boolean value) {
        if (value) {
            accessFlags |= ACC_PROTECTED;
        } else {
            accessFlags &= ~ACC_PROTECTED;
        }
    }

    public boolean isProtected() {
        return (accessFlags & ACC_PROTECTED) > 0;
    }

    public void setStatic(boolean value) {
        if (value) {
            accessFlags |= ACC_STATIC;
        } else {
            accessFlags &= ~ACC_STATIC;
        }
    }

    public boolean isStatic() {
        return (accessFlags & ACC_STATIC) > 0;
    }

    public void setFinal(boolean value) {
        if (value) {
            accessFlags |= ACC_FINAL;
        } else {
            accessFlags &= ~ACC_FINAL;
        }
    }

    public boolean isFinal() {
        return (accessFlags & ACC_FINAL) > 0;
    }

    public void setSynchronized(boolean value) {
        if (value) {
            accessFlags |= ACC_SYNCHRONIZED;
        } else {
            accessFlags &= ~ACC_SYNCHRONIZED;
        }
    }

    public boolean isSynchronized() {
        return (accessFlags & ACC_SYNCHRONIZED) > 0;
    }

    public void setVolatile(boolean value) {
        if (value) {
            accessFlags |= ACC_VOLATILE;
        } else {
            accessFlags &= ~ACC_VOLATILE;
        }
    }

    public boolean isVolatile() {
        return (accessFlags & ACC_VOLATILE) > 0;
    }

    public void setTransient(boolean value) {
        if (value) {
            accessFlags |= ACC_TRANSIENT;
        } else {
            accessFlags &= ~ACC_TRANSIENT;
        }
    }

    public boolean isTransient() {
        return (accessFlags & ACC_TRANSIENT) > 0;
    }

    public void setNative(boolean value) {
        if (value) {
            accessFlags |= ACC_NATIVE;
        } else {
            accessFlags &= ~ACC_NATIVE;
        }
    }

    public boolean isNative() {
        return (accessFlags & ACC_NATIVE) > 0;
    }


    public void setInterface(boolean value) {
        if (value) {
            accessFlags |= ACC_INTERFACE;
        } else {
            accessFlags &= ~ACC_INTERFACE;
        }
    }

    public boolean isInterface() {
        return (accessFlags & ACC_INTERFACE) > 0;
    }


    public void setAbstract(boolean value) {
        if (value) {
            accessFlags |= ACC_ABSTRACT;
        } else {
            accessFlags &= ~ACC_ABSTRACT;
        }
    }

    public boolean isAbstract() {
        return (accessFlags & ACC_ABSTRACT) > 0;
    }


    public void setStrict(boolean value) {
        if (value) {
            accessFlags |= ACC_STRICT;
        } else {
            accessFlags &= ~ACC_STRICT;
        }
    }

    public boolean isStrict() {
        return (accessFlags & ACC_STRICT) > 0;
    }


    public void setSuper(boolean value) {
        if (value) {
            accessFlags |= ACC_SUPER;
        } else {
            accessFlags &= ~ACC_SUPER;
        }
    }

    public boolean isSuper() {
        return (accessFlags & ACC_SUPER) > 0;
    }

}
