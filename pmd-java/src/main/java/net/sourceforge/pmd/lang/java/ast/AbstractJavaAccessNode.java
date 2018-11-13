/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

public abstract class AbstractJavaAccessNode extends AbstractJavaAnnotatableNode implements AccessNode {

    private int modifiers;

    public AbstractJavaAccessNode(int i) {
        super(i);
    }

    public AbstractJavaAccessNode(JavaParser parser, int i) {
        super(parser, i);
    }

    @Override
    public int getModifiers() {
        return this.modifiers;
    }

    @Override
    public void setModifiers(int modifiers) {
        this.modifiers = modifiers;
    }

    @Override
    public boolean isPublic() {
        return isModifier(PUBLIC);
    }

    @Override
    public void setPublic(boolean isPublic) {
        setModifier(isPublic, PUBLIC);
    }

    @Override
    public boolean isProtected() {
        return isModifier(PROTECTED);
    }

    @Override
    public void setProtected(boolean isProtected) {
        setModifier(isProtected, PROTECTED);
    }

    @Override
    public boolean isPrivate() {
        return isModifier(PRIVATE);
    }

    @Override
    public void setPrivate(boolean isPrivate) {
        setModifier(isPrivate, PRIVATE);
    }

    @Override
    public boolean isAbstract() {
        return isModifier(ABSTRACT);
    }

    @Override
    public void setAbstract(boolean isAbstract) {
        setModifier(isAbstract, ABSTRACT);
    }

    @Override
    public boolean isStatic() {
        return isModifier(STATIC);
    }

    @Override
    public void setStatic(boolean isStatic) {
        setModifier(isStatic, STATIC);
    }

    @Override
    public boolean isFinal() {
        return isModifier(FINAL);
    }

    @Override
    public void setFinal(boolean isFinal) {
        setModifier(isFinal, FINAL);
    }

    @Override
    public boolean isSynchronized() {
        return isModifier(SYNCHRONIZED);
    }

    @Override
    public void setSynchronized(boolean isSynchronized) {
        setModifier(isSynchronized, SYNCHRONIZED);
    }

    @Override
    public boolean isNative() {
        return isModifier(NATIVE);
    }

    @Override
    public void setNative(boolean isNative) {
        setModifier(isNative, NATIVE);
    }

    @Override
    public boolean isTransient() {
        return isModifier(TRANSIENT);
    }

    @Override
    public void setTransient(boolean isTransient) {
        setModifier(isTransient, TRANSIENT);
    }

    @Override
    public boolean isVolatile() {
        return isModifier(VOLATILE);
    }

    @Override
    public void setVolatile(boolean isVolative) {
        setModifier(isVolative, VOLATILE);
    }

    @Override
    public boolean isStrictfp() {
        return isModifier(STRICTFP);
    }

    @Override
    public void setStrictfp(boolean isStrictfp) {
        setModifier(isStrictfp, STRICTFP);
    }

    @Override
    public boolean isDefault() {
        return isModifier(DEFAULT);
    }

    @Override
    public void setDefault(boolean isDefault) {
        setModifier(isDefault, DEFAULT);
    }

    private boolean isModifier(int mask) {
        return (modifiers & mask) == mask;
    }

    private void setModifier(boolean enable, int mask) {
        if (enable) {
            this.modifiers |= mask;
        } else {
            this.modifiers &= ~mask;
        }
    }

    @Override
    public boolean isPackagePrivate() {
        return !isPrivate() && !isPublic() && !isProtected();
    }
}

