package net.sourceforge.pmd.ast;

public class AccessNode extends SimpleJavaNode {

    public static final int PUBLIC = 0x0001;
    public static final int PROTECTED = 0x0002;
    public static final int PRIVATE = 0x0004;
    public static final int ABSTRACT = 0x0008;
    public static final int STATIC = 0x0010;
    public static final int FINAL = 0x0020;
    public static final int SYNCHRONIZED = 0x0040;
    public static final int NATIVE = 0x0080;
    public static final int TRANSIENT = 0x0100;
    public static final int VOLATILE = 0x0200;
    public static final int STRICTFP = 0x1000;

    public AccessNode(int i) {
        super(i);
    }

    public AccessNode(JavaParser parser, int i) {
        super(parser, i);
    }

    private int modifiers;

    public void setModifiers(int m) {
        this.modifiers = m;
    }

    public boolean isPublic() {
        return (modifiers & PUBLIC) != 0;
    }

    public boolean isProtected() {
        return (modifiers & PROTECTED) != 0;
    }

    public boolean isPrivate() {
        return (modifiers & PRIVATE) != 0;
    }

    public boolean isStatic() {
        return (modifiers & STATIC) != 0;
    }

    public boolean isAbstract() {
        return (modifiers & ABSTRACT) != 0;
    }

    public boolean isFinal() {
        return (modifiers & FINAL) != 0;
    }

    public boolean isNative() {
        return (modifiers & NATIVE) != 0;
    }

    public boolean isStrictfp() {
        return (modifiers & STRICTFP) != 0;
    }

    public boolean isSynchronized() {
        return (modifiers & SYNCHRONIZED) != 0;
    }

    public boolean isTransient() {
        return (modifiers & TRANSIENT) != 0;
    }

    public boolean isVolatile() {
        return (modifiers & VOLATILE) != 0;
    }

    public void setPublic() {
        modifiers |= PUBLIC;
    }

    public void setPrivate() {
        modifiers |= PRIVATE;
    }

    public void setProtected() {
        modifiers |= PROTECTED;
    }

    public void setSynchronized() {
        modifiers |= SYNCHRONIZED;
    }

    public void setVolatile() {
        modifiers |= VOLATILE;
    }

    public void setAbstract() {
        modifiers |= ABSTRACT;
    }

    public void setStatic() {
        modifiers |= STATIC;
    }

    public void setTransient() {
        modifiers |= TRANSIENT;
    }

    public void setFinal() {
        modifiers |= FINAL;
    }

    public void setNative() {
        modifiers |= NATIVE;
    }

    public void setStrictfp() {
        modifiers |= STRICTFP;
    }

    /**
     * Removes the given modifier.
     */
    static int removeModifier(int modifiers, int mod) {
        return modifiers & ~mod;
    }

    public boolean isPackagePrivate() {
        return !isPrivate() && !isPublic() && !isProtected();
    }

    public String collectDumpedModifiers(String prefix) {
        String out = toString(prefix) + ":";
        if (isPackagePrivate()) {
            out += "(package private)";
        }
        if (isPrivate()) {
            out += "(private)";
        }
        if (isPublic()) {
            out += "(public)";
        }
        if (isProtected()) {
            out += "(protected)";
        }
        if (isAbstract()) {
            out += "(abstract)";
        }
        if (isStatic()) {
            out += "(static)";
        }
        if (isFinal()) {
            out += "(final)";
        }
        if (isSynchronized()) {
            out += "(synchronized)";
        }
        if (isNative()) {
            out += "(native)";
        }
        if (isStrictfp()) {
            out += "(strict)";
        }
        if (isTransient()) {
            out += "(transient)";
        }
        return out;
    }
}
