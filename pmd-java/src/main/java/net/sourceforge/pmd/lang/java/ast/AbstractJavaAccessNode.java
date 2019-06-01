/**
 * BSD-style license; for more info see http://pmd.sourceforge.net/license.html
 */

package net.sourceforge.pmd.lang.java.ast;

import net.sourceforge.pmd.annotation.InternalApi;

@Deprecated
@InternalApi
public abstract class AbstractJavaAccessNode extends AbstractJavaAnnotatableNode implements AccessNode {

    private int modifiers;

    @Deprecated
    @InternalApi
    public AbstractJavaAccessNode(int i) {
        super(i);
    }

    @Deprecated
    @InternalApi
    public AbstractJavaAccessNode(JavaParser parser, int i) {
        super(parser, i);
    }

    @Override
    public int getModifiers() {
        return this.modifiers;
    }

    @InternalApi
    @Deprecated
    @Override
    public void setModifiers(int modifiers) {
        this.modifiers = modifiers;
    }

    @Override
    public boolean isPublic() {
        return isModifier(PUBLIC);
    }

    @InternalApi
    @Deprecated
    @Override
    public void setPublic(boolean isPublic) {
        setModifier(isPublic, PUBLIC);
    }

    @Override
    public boolean isProtected() {
        return isModifier(PROTECTED);
    }

    @InternalApi
    @Deprecated
    @Override
    public void setProtected(boolean isProtected) {
        setModifier(isProtected, PROTECTED);
    }

    @Override
    public boolean isPrivate() {
        return isModifier(PRIVATE);
    }

    @InternalApi
    @Deprecated
    @Override
    public void setPrivate(boolean isPrivate) {
        setModifier(isPrivate, PRIVATE);
    }

    @Override
    public boolean isAbstract() {
        return isModifier(ABSTRACT);
    }

    @InternalApi
    @Deprecated
    @Override
    public void setAbstract(boolean isAbstract) {
        setModifier(isAbstract, ABSTRACT);
    }

    @Override
    public boolean isStatic() {
        return isModifier(STATIC);
    }

    @InternalApi
    @Deprecated
    @Override
    public void setStatic(boolean isStatic) {
        setModifier(isStatic, STATIC);
    }

    @Override
    public boolean isFinal() {
        return isModifier(FINAL);
    }

    @InternalApi
    @Deprecated
    @Override
    public void setFinal(boolean isFinal) {
        setModifier(isFinal, FINAL);
    }

    @Override
    public boolean isSynchronized() {
        return isModifier(SYNCHRONIZED);
    }

    @InternalApi
    @Deprecated
    @Override
    public void setSynchronized(boolean isSynchronized) {
        setModifier(isSynchronized, SYNCHRONIZED);
    }

    @Override
    public boolean isNative() {
        return isModifier(NATIVE);
    }

    @InternalApi
    @Deprecated
    @Override
    public void setNative(boolean isNative) {
        setModifier(isNative, NATIVE);
    }

    @Override
    public boolean isTransient() {
        return isModifier(TRANSIENT);
    }

    @InternalApi
    @Deprecated
    @Override
    public void setTransient(boolean isTransient) {
        setModifier(isTransient, TRANSIENT);
    }

    @Override
    public boolean isVolatile() {
        return isModifier(VOLATILE);
    }

    @InternalApi
    @Deprecated
    @Override
    public void setVolatile(boolean isVolative) {
        setModifier(isVolative, VOLATILE);
    }

    @Override
    public boolean isStrictfp() {
        return isModifier(STRICTFP);
    }

    @InternalApi
    @Deprecated
    @Override
    public void setStrictfp(boolean isStrictfp) {
        setModifier(isStrictfp, STRICTFP);
    }

    @Override
    public boolean isDefault() {
        return isModifier(DEFAULT);
    }

    @InternalApi
    @Deprecated
    @Override
    public void setDefault(boolean isDefault) {
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

