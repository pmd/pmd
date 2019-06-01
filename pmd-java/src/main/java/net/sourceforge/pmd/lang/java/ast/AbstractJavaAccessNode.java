/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;

@Deprecated
@InternalApi
public abstract class AbstractJavaAccessNode extends AbstractJavaAnnotatableNode implements AccessNode {

    private int modifiers;

    AbstractJavaAccessNode(int i) {
        super(i);
    }

    AbstractJavaAccessNode(JavaParser parser, int i) {
        super(parser, i);
    }

    @Override
    public int getModifiers() {
        return this.modifiers;
    }

    void setModifiers(int modifiers) {
        this.modifiers = modifiers;
    }

    @Override
    public boolean isPublic() {
        return isModifier(PUBLIC);
    }

    @InternalApi
    @Deprecated
    public void setPublic(boolean isPublic) {
        setModifier(isPublic, PUBLIC);
    }

    @Override
    public boolean isProtected() {
        return isModifier(PROTECTED);
    }

    void setProtected(boolean isProtected) {
        setModifier(isProtected, PROTECTED);
    }

    @Override
    public boolean isPrivate() {
        return isModifier(PRIVATE);
    }

    void setPrivate(boolean isPrivate) {
        setModifier(isPrivate, PRIVATE);
    }

    @Override
    public boolean isAbstract() {
        return isModifier(ABSTRACT);
    }

    void setAbstract(boolean isAbstract) {
        setModifier(isAbstract, ABSTRACT);
    }

    @Override
    public boolean isStatic() {
        return isModifier(STATIC);
    }

    void setStatic(boolean isStatic) {
        setModifier(isStatic, STATIC);
    }

    @Override
    public boolean isFinal() {
        return isModifier(FINAL);
    }

    void setFinal(boolean isFinal) {
        setModifier(isFinal, FINAL);
    }

    @Override
    public boolean isSynchronized() {
        return isModifier(SYNCHRONIZED);
    }

    void setSynchronized(boolean isSynchronized) {
        setModifier(isSynchronized, SYNCHRONIZED);
    }

    @Override
    public boolean isNative() {
        return isModifier(NATIVE);
    }

    void setNative(boolean isNative) {
        setModifier(isNative, NATIVE);
    }

    @Override
    public boolean isTransient() {
        return isModifier(TRANSIENT);
    }

    void setTransient(boolean isTransient) {
        setModifier(isTransient, TRANSIENT);
    }

    @Override
    public boolean isVolatile() {
        return isModifier(VOLATILE);
    }

    void setVolatile(boolean isVolative) {
        setModifier(isVolative, VOLATILE);
    }

    @Override
    public boolean isStrictfp() {
        return isModifier(STRICTFP);
    }

    void setStrictfp(boolean isStrictfp) {
        setModifier(isStrictfp, STRICTFP);
    }

    @Override
    public boolean isDefault() {
        return isModifier(DEFAULT);
    }

    void setDefault(boolean isDefault) {
        setModifier(isDefault, DEFAULT);
    }

    private boolean isModifier(int mask) {
        return (modifiers & mask) == mask;
    }

    @InternalApi
    @Deprecated
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

