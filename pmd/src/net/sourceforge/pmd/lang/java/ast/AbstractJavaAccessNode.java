package net.sourceforge.pmd.lang.java.ast;

public abstract class AbstractJavaAccessNode extends AbstractJavaNode implements AccessNode {

    private int modifiers;

    public AbstractJavaAccessNode(int i) {
        super(i);
    }

    public AbstractJavaAccessNode(JavaParser parser, int i) {
        super(parser, i);
    }

    public int getModifiers() {
        return this.modifiers;
    }

    public void setModifiers(int modifiers) {
        this.modifiers = modifiers;
    }

    public boolean isPublic() {
        return isModifier(PUBLIC);
    }

    /**
     * @deprecated Use setPublic(boolean) instead.
     */
    @Deprecated
    public void setPublic() {
        setPublic(true);
    }

    public void setPublic(boolean isPublic) {
        setModifier(isPublic, PUBLIC);
    }

    public boolean isProtected() {
        return isModifier(PROTECTED);
    }

    /**
     * @deprecated Use setProtected(boolean) instead.
     */
    @Deprecated
    public void setProtected() {
        setProtected(true);
    }

    public void setProtected(boolean isProtected) {
        setModifier(isProtected, PROTECTED);
    }

    public boolean isPrivate() {
        return isModifier(PRIVATE);
    }

    /**
     * @deprecated Use setPrivate(boolean) instead.
     */
    @Deprecated
    public void setPrivate() {
        setPrivate(true);
    }

    public void setPrivate(boolean isPrivate) {
        setModifier(isPrivate, PRIVATE);
    }

    public boolean isAbstract() {
        return isModifier(ABSTRACT);
    }

    /**
     * @deprecated Use setAbstract(boolean) instead.
     */
    @Deprecated
    public void setAbstract() {
        setAbstract(true);
    }

    public void setAbstract(boolean isAbstract) {
        setModifier(isAbstract, ABSTRACT);
    }

    public boolean isStatic() {
        return isModifier(STATIC);
    }

    /**
     * @deprecated Use setStatic(boolean) instead.
     */
    @Deprecated
    public void setStatic() {
        setStatic(true);
    }

    public void setStatic(boolean isStatic) {
        setModifier(isStatic, STATIC);
    }

    public boolean isFinal() {
        return isModifier(FINAL);
    }

    /**
     * @deprecated Use setFinal(boolean) instead.
     */
    @Deprecated
    public void setFinal() {
        setFinal(true);
    }

    public void setFinal(boolean isFinal) {
        setModifier(isFinal, FINAL);
    }

    public boolean isSynchronized() {
        return isModifier(SYNCHRONIZED);
    }

    /**
     * @deprecated Use setSynchronized(boolean) instead.
     */
    @Deprecated
    public void setSynchronized() {
        setSynchronized(true);
    }

    public void setSynchronized(boolean isSynchronized) {
        setModifier(isSynchronized, SYNCHRONIZED);
    }

    public boolean isNative() {
        return isModifier(NATIVE);
    }

    /**
     * @deprecated Use setNative(boolean) instead.
     */
    @Deprecated
    public void setNative() {
        setNative(true);
    }

    public void setNative(boolean isNative) {
        setModifier(isNative, NATIVE);
    }

    public boolean isTransient() {
        return isModifier(TRANSIENT);
    }

    /**
     * @deprecated Use setTransient(boolean) instead.
     */
    @Deprecated
    public void setTransient() {
        setTransient(true);
    }

    public void setTransient(boolean isTransient) {
        setModifier(isTransient, TRANSIENT);
    }

    public boolean isVolatile() {
        return isModifier(VOLATILE);
    }

    /**
     * @deprecated Use setVolatile(boolean) instead.
     */
    @Deprecated
    public void setVolatile() {
        setVolatile(true);
    }

    public void setVolatile(boolean isVolative) {
        setModifier(isVolative, VOLATILE);
    }

    public boolean isStrictfp() {
        return isModifier(STRICTFP);
    }

    /**
     * @deprecated Use setStrictfp(boolean) instead.
     */
    @Deprecated
    public void setStrictfp() {
        setStrictfp(true);
    }

    public void setStrictfp(boolean isStrictfp) {
        setModifier(isStrictfp, STRICTFP);
    }

    private final boolean isModifier(int mask) {
        return (modifiers & mask) == mask;
    }

    private void setModifier(boolean enable, int mask) {
        if (enable) {
            this.modifiers |= mask;
        } else {
            this.modifiers &= ~mask;
        }
    }

    public boolean isPackagePrivate() {
        return !isPrivate() && !isPublic() && !isProtected();
    }
}
